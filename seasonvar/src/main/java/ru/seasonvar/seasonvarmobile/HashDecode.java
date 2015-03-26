package ru.seasonvar.seasonvarmobile;

/**
 * Created by Andrey_Demidenko on 3/26/2015 12:14 PM.
 */
public class HashDecode {

    private static final String hashKey = "JpvnsR03Tmwu9xgaGLUXztb7H=\n" +
            "fNW5elVDyZIiMoQ1B826cd4YkC";

    public static String decode(String code) {
        String param = code;
        String loc_2 = "";
        try {
            //define variables
            int[] loc_3 = {0, 0, 0, 0};
            int[] loc_4 = {0, 0, 0};

//            #-- define hash parameters for decoding
            String dec = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
            String hash1 = hashKey.split("\\n")[0];
            String hash2 = hashKey.split("\\n")[1];

//            #-- decode
            for (int i = 0; i < hash1.length(); i++) {
                char re1 = hash1.charAt(i);
                char re2 = hash2.charAt(i);

                param = param.replaceAll(String.valueOf(re1), "___");
                param = param.replaceAll(String.valueOf(re2), String.valueOf(re1));
                param = param.replaceAll("___", String.valueOf(re2));
            }
            int i = 0;
            while (i < param.length()) {
                int j = 0;
                while (j < 4 && i + j < param.length()) {
                    loc_3[j] = dec.indexOf(param.charAt(i + j));
                    j = j + 1;
                }
                loc_4[0] = (loc_3[0] << 2) + ((loc_3[1] & 48) >> 4);
                loc_4[1] = ((loc_3[1] & 15) << 4) + ((loc_3[2] & 60) >> 2);
                loc_4[2] = ((loc_3[2] & 3) << 6) + loc_3[3];
                j = 0;
                while (j < 3) {
                    if (loc_3[j + 1] == 64) {
                        break;
                    }
                    loc_2 += (char) loc_4[j];
                    j = j + 1;
                }
                i = i + 4;
            }
        } catch (Exception e) {
            loc_2 = "";
        }
        return loc_2;
    }
}
