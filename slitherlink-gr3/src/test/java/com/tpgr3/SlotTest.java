package com.tpgr3;

import junit.framework.TestCase;

public class SlotTest extends TestCase {

    public void testActionner() {
        Slot slot = new Slot(0, 0);
        assertEquals(' ', slot.afficher());
        slot.actionner();
        assertEquals('-', slot.afficher());
        slot.actionner();
        assertEquals('X', slot.afficher());
        slot.actionner();
        assertEquals(' ', slot.afficher());
    }

}