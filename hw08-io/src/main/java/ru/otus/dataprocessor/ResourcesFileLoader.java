package ru.otus.dataprocessor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.List;
import ru.otus.model.Measurement;

public class ResourcesFileLoader implements Loader {

    private final String fileName;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() {
        try (InputStream is = ResourcesFileLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new FileProcessException("File not found: " + fileName);
            }
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(is, new TypeReference<List<Measurement>>() {});
        } catch (Exception e) {
            throw new FileProcessException(e);
        }
    }
}
