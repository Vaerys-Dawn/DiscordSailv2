package com.github.vaerys.validations;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.masterobjects.CommandObject;
import junit.framework.TestCase;

/**
 * used to test the messageChannel setting enum
 *
 * @author C0bra5
 */
public class Test_ChannelSetting extends TestCase {


    /**
     * Checks if the name field in the enum is properly set
     */
//    @Test
    public void testNames() {
        for (ChannelSetting setting : ChannelSetting.values()) {
            // name/tostring related
            if (setting.toString() == null) {
                fail(String.format("The \"%s\" messageChannel setting's name cannot be unset.", setting.name()));
            }
            if (setting.toString().trim().equalsIgnoreCase("")) {
                fail(String.format("The \"%s\" messageChannel setting's name cannot be blank.", setting.name()));
            }
        }
    }

    /**
     * Checks if the description field in the enum is properly set
     */
//    @Test
    public void testDescription() {
        for (ChannelSetting setting : ChannelSetting.values()) {
            if (setting.getDesc() == null) {
                fail(String.format("The \"%s\" messageChannel setting's description cannot be unset.", setting.name()));
            }
            if (setting.getDesc().trim().equalsIgnoreCase("")) {
                fail(String.format("The \"%s\" messageChannel setting's description cannot be blank.", setting.name()));
            }
        }
    }


    /**
     * Checks if the {@link ChannelSetting#getDesc(CommandObject)} method
     * returns the same thing as {@link ChannelSetting#getDesc()} method
     */
//    @Test
    public void testDescriptionWithNullParameter() {
        for (ChannelSetting setting : ChannelSetting.values()) {
            // other getAllToggles desc
            if (setting.getDesc(null) == null) {
                fail(String.format("The \"%s\" messageChannel setting's description cannot be unset.", setting.name()));
            }
            if (setting.getDesc(null).trim().equalsIgnoreCase("")) {
                fail(String.format("The \"%s\" messageChannel setting's description cannot be blank.", setting.name()));
            }
            if (!setting.getDesc(null).equals(setting.getDesc())) {
                fail("The getDesc method should output the same string as the getDesc method that has no parameter");
            }
        }
    }
}
