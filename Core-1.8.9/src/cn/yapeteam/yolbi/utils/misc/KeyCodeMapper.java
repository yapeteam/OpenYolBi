package cn.yapeteam.yolbi.utils.misc;

import java.util.HashMap;
import java.util.Map;

public class KeyCodeMapper {

    private static final Map<Integer, Integer> keyCodeMap = new HashMap<>();

    static {
        // Mapping LWJGL keycodes to VirtualKeyBoard constants
        // Alphanumeric keys
        keyCodeMap.put(48, 0x30); // LWJGL KeyCode 48 maps to VK_0
        keyCodeMap.put(49, 0x31); // LWJGL KeyCode 49 maps to VK_1
        keyCodeMap.put(50, 0x32); // LWJGL KeyCode 50 maps to VK_2
        keyCodeMap.put(51, 0x33); // LWJGL KeyCode 51 maps to VK_3
        keyCodeMap.put(52, 0x34); // LWJGL KeyCode 52 maps to VK_4
        keyCodeMap.put(53, 0x35); // LWJGL KeyCode 53 maps to VK_5
        keyCodeMap.put(54, 0x36); // LWJGL KeyCode 54 maps to VK_6
        keyCodeMap.put(55, 0x37); // LWJGL KeyCode 55 maps to VK_7
        keyCodeMap.put(56, 0x38); // LWJGL KeyCode 56 maps to VK_8
        keyCodeMap.put(57, 0x39); // LWJGL KeyCode 57 maps to VK_9
        keyCodeMap.put(65, 0x41); // LWJGL KeyCode 65 maps to VK_A
        keyCodeMap.put(66, 0x42); // LWJGL KeyCode 66 maps to VK_B
        keyCodeMap.put(67, 0x43); // LWJGL KeyCode 67 maps to VK_C
        keyCodeMap.put(68, 0x44); // LWJGL KeyCode 68 maps to VK_D
        keyCodeMap.put(69, 0x45); // LWJGL KeyCode 69 maps to VK_E
        keyCodeMap.put(70, 0x46); // LWJGL KeyCode 70 maps to VK_F
        keyCodeMap.put(71, 0x47); // LWJGL KeyCode 71 maps to VK_G
        keyCodeMap.put(72, 0x48); // LWJGL KeyCode 72 maps to VK_H
        keyCodeMap.put(73, 0x49); // LWJGL KeyCode 73 maps to VK_I
        keyCodeMap.put(74, 0x4A); // LWJGL KeyCode 74 maps to VK_J
        keyCodeMap.put(75, 0x4B); // LWJGL KeyCode 75 maps to VK_K
        keyCodeMap.put(76, 0x4C); // LWJGL KeyCode 76 maps to VK_L
        keyCodeMap.put(77, 0x4D); // LWJGL KeyCode 77 maps to VK_M
        keyCodeMap.put(78, 0x4E); // LWJGL KeyCode 78 maps to VK_N
        keyCodeMap.put(79, 0x4F); // LWJGL KeyCode 79 maps to VK_O
        keyCodeMap.put(80, 0x50); // LWJGL KeyCode 80 maps to VK_P
        keyCodeMap.put(81, 0x51); // LWJGL KeyCode 81 maps to VK_Q
        keyCodeMap.put(82, 0x52); // LWJGL KeyCode 82 maps to VK_R
        keyCodeMap.put(83, 0x53); // LWJGL KeyCode 83 maps to VK_S
        keyCodeMap.put(84, 0x54); // LWJGL KeyCode 84 maps to VK_T
        keyCodeMap.put(85, 0x55); // LWJGL KeyCode 85 maps to VK_U
        keyCodeMap.put(86, 0x56); // LWJGL KeyCode 86 maps to VK_V
        keyCodeMap.put(87, 0x57); // LWJGL KeyCode 87 maps to VK_W
        keyCodeMap.put(88, 0x58); // LWJGL KeyCode 88 maps to VK_X
        keyCodeMap.put(89, 0x59); // LWJGL KeyCode 89 maps to VK_Y
        keyCodeMap.put(90, 0x5A); // LWJGL KeyCode 90 maps to VK_Z

        // Function keys
        keyCodeMap.put(290, 0x70); // LWJGL KeyCode 290 maps to VK_F1
        keyCodeMap.put(291, 0x71); // LWJGL KeyCode 291 maps to VK_F2
        keyCodeMap.put(292, 0x72); // LWJGL KeyCode 292 maps to VK_F3
        keyCodeMap.put(293, 0x73); // LWJGL KeyCode 293 maps to VK_F4
        keyCodeMap.put(294, 0x74); // LWJGL KeyCode 294 maps to VK_F5
        keyCodeMap.put(295, 0x75); // LWJGL KeyCode 295 maps to VK_F6
        keyCodeMap.put(296, 0x76); // LWJGL KeyCode 296 maps to VK_F7
        keyCodeMap.put(297, 0x77); // LWJGL KeyCode 297 maps to VK_F8
        keyCodeMap.put(298, 0x78); // LWJGL KeyCode 298 maps to VK_F9
        keyCodeMap.put(299, 0x79); // LWJGL KeyCode 299 maps to VK_F10
        keyCodeMap.put(300, 0x7A); // LWJGL KeyCode 300 maps to VK_F11
        keyCodeMap.put(301, 0x7B); // LWJGL KeyCode 301 maps to VK_F12

        // Control keys
        keyCodeMap.put(256, 0x1B); // LWJGL KeyCode 256 maps to VK_ESCAPE
        keyCodeMap.put(257, 0x0D); // LWJGL KeyCode 257 maps to VK_RETURN
        keyCodeMap.put(258, 0x09); // LWJGL KeyCode 258 maps to VK_TAB
        keyCodeMap.put(259, 0x08); // LWJGL KeyCode 259 maps to VK_BACK
        keyCodeMap.put(260, 0x13); // LWJGL KeyCode 260 maps to VK_PAUSE
        keyCodeMap.put(261, 0x2C); // LWJGL KeyCode 261 maps to VK_SNAPSHOT
        keyCodeMap.put(262, 0x25); // LWJGL KeyCode 262 maps to VK_LEFT
        keyCodeMap.put(263, 0x27); // LWJGL KeyCode 263 maps to VK_RIGHT
        keyCodeMap.put(264, 0x28); // LWJGL KeyCode 264 maps to VK_DOWN
        keyCodeMap.put(265, 0x26); // LWJGL KeyCode 265 maps to VK_UP
        keyCodeMap.put(266, 0x21); // LWJGL KeyCode 266 maps to VK_PRIOR
        keyCodeMap.put(267, 0x22); // LWJGL KeyCode 267 maps to VK_NEXT
        keyCodeMap.put(268, 0x23); // LWJGL KeyCode 268 maps to VK_END
        keyCodeMap.put(269, 0x24); // LWJGL KeyCode 269 maps to VK_HOME
        keyCodeMap.put(280, 0x90); // LWJGL KeyCode 280 maps to VK_NUMLOCK
        keyCodeMap.put(281, 0x91); // LWJGL KeyCode 281 maps to VK_SCROLL

        // Numpad keys
        keyCodeMap.put(320, 0x60); // LWJGL KeyCode 320 maps to VK_NUMPAD0
        keyCodeMap.put(321, 0x61); // LWJGL KeyCode 321 maps to VK_NUMPAD1
        keyCodeMap.put(322, 0x62); // LWJGL KeyCode 322 maps to VK_NUMPAD2
        keyCodeMap.put(323, 0x63); // LWJGL KeyCode 323 maps to VK_NUMPAD3
        keyCodeMap.put(324, 0x64); // LWJGL KeyCode 324 maps to VK_NUMPAD4
        keyCodeMap.put(325, 0x65); // LWJGL KeyCode 325 maps to VK_NUMPAD5
        keyCodeMap.put(326, 0x66); // LWJGL KeyCode 326 maps to VK_NUMPAD6
        keyCodeMap.put(327, 0x67); // LWJGL KeyCode 327 maps to VK_NUMPAD7
        keyCodeMap.put(328, 0x68); // LWJGL KeyCode 328 maps to VK_NUMPAD8
        keyCodeMap.put(329, 0x69); // LWJGL KeyCode 329 maps to VK_NUMPAD9
        keyCodeMap.put(330, 0x6E); // LWJGL KeyCode 330 maps to VK_DECIMAL
        keyCodeMap.put(331, 0x6F); // LWJGL KeyCode 331 maps to VK_DIVIDE
        keyCodeMap.put(332, 0x6A); // LWJGL KeyCode 332 maps to VK_MULTIPLY
        keyCodeMap.put(333, 0x6B); // LWJGL KeyCode 333 maps to VK_ADD
        keyCodeMap.put(334, 0x6D); // LWJGL KeyCode 334 maps to VK_SUBTRACT

        // Symbol keys
        keyCodeMap.put(45, 0x2D); // LWJGL KeyCode 45 maps to VK_INSERT
        keyCodeMap.put(46, 0x2E); // LWJGL KeyCode 46 maps to VK_DELETE
        keyCodeMap.put(91, 0xDB); // LWJGL KeyCode 91 maps to VK_OEM_4
        keyCodeMap.put(92, 0xDC); // LWJGL KeyCode 92 maps to VK_OEM_5
        keyCodeMap.put(93, 0xDD); // LWJGL KeyCode 93 maps to VK_OEM_6
        keyCodeMap.put(94, 0xDE); // LWJGL KeyCode 94 maps to VK_OEM_7
        keyCodeMap.put(59, 0xBA); // LWJGL KeyCode 59 maps to VK_OEM_1
        keyCodeMap.put(61, 0xBB); // LWJGL KeyCode 61 maps to VK_OEM_PLUS
        keyCodeMap.put(44, 0xBC); // LWJGL KeyCode 44 maps to VK_OEM_COMMA
        keyCodeMap.put(45, 0xBD); // LWJGL KeyCode 45 maps to VK_OEM_MINUS
        keyCodeMap.put(46, 0xBE); // LWJGL KeyCode 46 maps to VK_OEM_PERIOD
        keyCodeMap.put(47, 0xBF); // LWJGL KeyCode 47 maps to VK_OEM_2
        keyCodeMap.put(96, 0xC0); // LWJGL KeyCode 96 maps to VK_OEM_3

        // Modifier keys
        keyCodeMap.put(340, 0xA0); // LWJGL KeyCode 340 maps to VK_LSHIFT
        keyCodeMap.put(341, 0xA2); // LWJGL KeyCode 341 maps to VK_LCONTROL
        keyCodeMap.put(342, 0xA4); // LWJGL KeyCode 342 maps to VK_LMENU (Alt)
        keyCodeMap.put(344, 0xA1); // LWJGL KeyCode 344 maps to VK_RSHIFT
        keyCodeMap.put(345, 0xA3); // LWJGL KeyCode 345 maps to VK_RCONTROL
        keyCodeMap.put(346, 0xA5); // LWJGL KeyCode 346 maps to VK_RMENU (Alt)
        keyCodeMap.put(343, 0x5B); // LWJGL KeyCode 343 maps to VK_LWIN (Left Windows)
        keyCodeMap.put(347, 0x5C); // LWJGL KeyCode 347 maps to VK_RWIN (Right Windows)
    }

    public static int mapLWJGLKeyCode(int KeyCode) {
        return keyCodeMap.getOrDefault(KeyCode, -1); // Return -1 if keycode is not mapped
    }
}
