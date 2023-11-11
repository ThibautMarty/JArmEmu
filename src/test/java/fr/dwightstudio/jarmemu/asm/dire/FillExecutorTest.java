package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FillExecutorTest extends JArmEmuTest {

    FillExecutor FILL = new FillExecutor();
    StateContainer container;

    @BeforeEach
    void setUp() {
        container = new StateContainer();

        for (int i = -1024 ; i < 1024 ; i += 4) {
            container.memory.putWord(i, -1);
        }
    }

    @Test
    void normalTest() {
        Random random = new Random();

        for (int i = 0 ; i < 32 ; i ++) {
            setUp();
            int r = random.nextInt(100);

            FILL.apply(container, String.valueOf(r), 0);

            for (int j = 0 ; j < r ; j++) {
                assertEquals((byte) 0, container.memory.getByte(j));
            }

            assertEquals((byte) 0xFF, container.memory.getByte(-4));
            assertEquals((byte) 0xFF, container.memory.getByte(r));
        }
    }

    @Test
    void oneSizeTest() {
        Random random = new Random();

        for (int i = 0 ; i < 32 ; i ++) {
            setUp();
            int r = random.nextInt(100);

            FILL.apply(container, r + ", 0b00101111, 1", 0);

            for (int j = 0 ; j < r ; j++) {
                assertEquals((byte) 0b00101111, container.memory.getByte(j));
            }

            assertEquals((byte) 0xFF, container.memory.getByte(-4));
            assertEquals((byte) 0xFF, container.memory.getByte(r));
        }
    }

    @Test
    void twoSizeTest() {
        Random random = new Random();

        for (int i = 0 ; i < 32 ; i ++) {
            setUp();
            int r = random.nextInt(100);

            FILL.apply(container, r + ", 0b0010111100001010, 2", 0);

            for (int j = 0 ; j < r ; j++) {
                if (j % 2 == 0) assertEquals((byte) 0b00101111, container.memory.getByte(j));
                if (j % 2 == 1) assertEquals((byte) 0b00001010, container.memory.getByte(j));
            }

            assertEquals((byte) 0xFF, container.memory.getByte(-4));
            assertEquals((byte) 0xFF, container.memory.getByte(r));
        }
    }

    @Test
    void threeSizeTest() {
        Random random = new Random();

        for (int i = 0 ; i < 32 ; i ++) {
            setUp();
            int r = random.nextInt(100);

            FILL.apply(container, r + ", 0b001011110000101011111111, 3", 0);

            for (int j = 0 ; j < r ; j++) {
                if (j % 3 == 0) assertEquals((byte) 0b00101111, container.memory.getByte(j));
                if (j % 3 == 1) assertEquals((byte) 0b00001010, container.memory.getByte(j));
                if (j % 3 == 2) assertEquals((byte) 0b11111111, container.memory.getByte(j));
            }

            assertEquals((byte) 0xFF, container.memory.getByte(-4));
            assertEquals((byte) 0xFF, container.memory.getByte(r));
        }
    }

    @Test
    void fourSizeTest() {
        Random random = new Random();

        for (int i = 0 ; i < 32 ; i ++) {
            setUp();
            int r = random.nextInt(100);

            FILL.apply(container, r + ", 0b00101111000010101111111100000001, 4", 0);

            for (int j = 0 ; j < r ; j++) {
                if (j % 4 == 0) assertEquals((byte) 0b00101111, container.memory.getByte(j));
                if (j % 4 == 1) assertEquals((byte) 0b00001010, container.memory.getByte(j));
                if (j % 4 == 2) assertEquals((byte) 0b11111111, container.memory.getByte(j));
                if (j % 4 == 3) assertEquals((byte) 0b00000001, container.memory.getByte(j));
            }

            assertEquals((byte) 0xFF, container.memory.getByte(-4));
            assertEquals((byte) 0xFF, container.memory.getByte(r));
        }
    }

    @Test
    void highSizeTest() {
        Random random = new Random();

        for (int i = 0 ; i < 32 ; i ++) {
            setUp();
            int r = random.nextInt(100);

            FILL.apply(container, r + ", 0b00101111000010101111111100000001, 17", 0);

            for (int j = 0 ; j < r ; j++) {
                if (j % 17 == 13) assertEquals((byte) 0b00101111, container.memory.getByte(j));
                if (j % 17 == 14) assertEquals((byte) 0b00001010, container.memory.getByte(j));
                if (j % 17 == 15) assertEquals((byte) 0b11111111, container.memory.getByte(j));
                if (j % 17 == 16) assertEquals((byte) 0b00000001, container.memory.getByte(j));
                if (j % 17 < 13) assertEquals((byte) 0b00000000, container.memory.getByte(j));
            }

            assertEquals((byte) 0xFF, container.memory.getByte(-4));
            assertEquals((byte) 0xFF, container.memory.getByte(r));
        }
    }

    @Test
    void higherSizeTest() {
        Random random = new Random();

        for (int i = 0 ; i < 32 ; i ++) {
            setUp();
            int r = random.nextInt(100);

            FILL.apply(container, r + ", 0b00101111000010101111111100000001, 123", 0);

            for (int j = 0 ; j < r ; j++) {
                assertEquals((byte) 0b00000000, container.memory.getByte(j));
            }

            assertEquals((byte) 0xFF, container.memory.getByte(-4));
            assertEquals((byte) 0xFF, container.memory.getByte(r));
        }
    }

}