package homework;

import java.util.*;

public class CustomerService {

    private final NavigableMap<Customer, String> customerMap =
            new TreeMap<>(Comparator.comparingLong(Customer::getScores));

    public void add(Customer customer, String data) {
        customerMap.put(customer, data);
    }

    public Map.Entry<Customer, String> getSmallest() {
        return copyEntry(customerMap.firstEntry());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        return copyEntry(customerMap.higherEntry(customer));
    }

    private Map.Entry<Customer, String> copyEntry(Map.Entry<Customer, String> entry) {
        if (entry == null) return null;
        Customer c = entry.getKey();
        return new AbstractMap.SimpleEntry<>(new Customer(c.getId(), c.getName(), c.getScores()), entry.getValue());
    }
}
