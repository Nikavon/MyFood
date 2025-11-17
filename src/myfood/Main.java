package myfood;

import easyaccept.EasyAccept;

public class Main {
    
    public static void main(String[] args) throws Exception {
        String facade = "myfood.Facade";
        EasyAccept.main(new String[] { facade, "test/us1_1.txt", });
        EasyAccept.main(new String[] { facade, "test/us1_2.txt", });
        EasyAccept.main(new String[] { facade, "test/us2_1.txt", });
        EasyAccept.main(new String[] { facade, "test/us2_2.txt", });
        EasyAccept.main(new String[] { facade, "test/us3_1.txt", });
        EasyAccept.main(new String[] { facade, "test/us3_2.txt", });
        EasyAccept.main(new String[] { facade, "test/us4_1.txt", });
        EasyAccept.main(new String[] { facade, "test/us4_2.txt", });
    }
}
