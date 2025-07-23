import annotation.After;
import annotation.Before;
import annotation.Test;

public class MyTests {

    private int counter = 0;

    @Before
    public void init() {
        counter = 42;
        System.out.println("Init test");
    }

    @Test
    public void testSuccess() {
        System.out.println("Running testSuccess, counter = " + counter);
    }

    @Test
    public void testFail() {
        System.out.println("Running testFail");
        throw new RuntimeException("Test failed intentionally");
    }

    @After
    public void cleanup() {
        System.out.println("Cleaning up");
    }
}
