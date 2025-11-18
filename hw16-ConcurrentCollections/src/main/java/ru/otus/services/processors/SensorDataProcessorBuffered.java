package ru.otus.services.processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;
import ru.otus.lib.SensorDataBufferedWriter;

@SuppressWarnings({"java:S1068", "java:S125"})
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final List<SensorData> dataBuffer;
    private final Lock lock;

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
        this.dataBuffer = new ArrayList<>(bufferSize);
        this.lock = new ReentrantLock();
    }

    @Override
    public void process(SensorData data) {
        if (data.getValue() == null || data.getValue().isNaN()) {
            return;
        }

        lock.lock();
        try {
            dataBuffer.add(data);

            Collections.sort(dataBuffer, (d1, d2) -> d1.getMeasurementTime().compareTo(d2.getMeasurementTime()));

            if (dataBuffer.size() >= bufferSize) {
                flush();
            }
        } finally {
            lock.unlock();
        }
    }

    public void flush() {
        List<SensorData> dataToWrite;

        lock.lock();
        try {
            if (dataBuffer.isEmpty()) {
                return;
            }

            // Создаем копию для записи и очищаем буфер
            dataToWrite = new ArrayList<>(dataBuffer);
            dataBuffer.clear();
        } finally {
            lock.unlock();
        }

        try {
            writer.writeBufferedData(dataToWrite);
        } catch (Exception e) {
            log.error("Ошибка в процессе записи буфера", e);
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}
