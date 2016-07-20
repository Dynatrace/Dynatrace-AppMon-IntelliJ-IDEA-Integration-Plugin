package com.dynatrace.diagnostics.codelink;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DynatraceClientVersionTest {

    @Test
    public void compareTest() {
        ClientVersion version = new ClientVersion(1, 2, 3, 4);
        assertTrue(new ClientVersion(1, 2, 3, 4).compareTo(version) == 0);
        assertTrue(new ClientVersion(2, 2, 3, 4).compareTo(version) == 1);
        assertTrue(new ClientVersion(1, 3, 3, 4).compareTo(version) == 1);
        assertTrue(new ClientVersion(0, 1, 2, 3).compareTo(version) == -1);
        assertTrue(new ClientVersion(1, 2, 3, 3).compareTo(version) == -1);
        assertTrue(new ClientVersion(1, 3, 4, 5).compareTo(version) == 1);
    }


    @Test
    public void fromStringGivenValidVersion() throws Exception {
        assertTrue(ClientVersion.fromString("6.3.5.13223").equals(new ClientVersion(6, 3, 5, 13223)));
        assertTrue(ClientVersion.fromString("0.3.5.13223").equals(new ClientVersion(0, 3, 5, 13223)));
        assertTrue(!ClientVersion.fromString("5.3.5.13223").equals(new ClientVersion(0, 3, 5, 13223)));
        assertTrue(!ClientVersion.fromString("5.3.5.0").equals(new ClientVersion(0, 3, 5, 0)));
    }

    @Test
    public void fromStringGivenInvalidVersion() {
        try {
            ClientVersion.fromString("1.2.3");
            fail("Exception not thrown when expected to do");
        } catch(IllegalArgumentException e) {
        }
        try {
            ClientVersion.fromString("");
            fail("Exception not thrown when expected to do");
        } catch(IllegalArgumentException e) {
        }
        try {
            ClientVersion.fromString("1.2.3");
            fail("Exception not thrown when expected to do");
        } catch(IllegalArgumentException e) {
        }
        try {
            ClientVersion.fromString("string.21.23.2");
            fail("Exception not thrown when expected to do");
        } catch(NumberFormatException e) {
        }
    }
}
