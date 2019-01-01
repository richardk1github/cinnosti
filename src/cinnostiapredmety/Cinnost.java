package cinnostiapredmety;

import java.util.Comparator;
import utils.PrvekSNazvem;
import utils.Sloupce;
import utils.Sloupec;

public class Cinnost extends PrvekSNazvem {

    public static final String ID_PREDMETU = "idPredmetu";
    public static final String PRIORITA = "priorita";
    public static final String POPIS = "popisCinnSPredmetem";

    private static final Sloupce SLOUPCE = new Sloupce(Cinnost.class);

    public static final Comparator<Cinnost> DLE_PRIORITY = (Cinnost o1, Cinnost o2) -> o1.getPriorita().compareTo(o2.getPriorita());
    public static final Comparator<Cinnost> DLE_PREDMETU = (Cinnost o1, Cinnost o2) -> {
        int i = o1.getPredmet().getNazev().compareTo(o2.getPredmet().getNazev());
        return i == 0 ? o1.getPriorita().compareTo(o2.getPriorita()) : i;
    };
    public static final Comparator<Cinnost> DLE_CINNOSTI = (Cinnost o1, Cinnost o2) -> o1.getNazev().compareTo(o2.getNazev());

    private PrvekSNazvem predmet;
    private Double priorita;
    private String popisCinnSPredmetem;

    public Cinnost() {
    }

    public static void staticInit() {
        SLOUPCE.addSloupec(new Sloupec<>(PrvekSNazvem.ID, "ID činnosti s předmětem", Integer.class, 10));
        SLOUPCE.addSloupec(new Sloupec<>(PrvekSNazvem.NAZEV, "název činnosti", String.class, 20));
        SLOUPCE.addSloupec(new Sloupec<>("predmet", "Předmět", CinnostiModel.instance.M_PREDMETY));
        SLOUPCE.addSloupec(new Sloupec<>(POPIS, "Popis", String.class, 40));
        SLOUPCE.addSloupec(new Sloupec<>(PRIORITA, PRIORITA, Double.class, 10));
    }

    public PrvekSNazvem getPredmet() {
        return predmet;
    }

    public void setPredmet(PrvekSNazvem predmet) {
        this.predmet = predmet;
    }

    public Double getPriorita() {
        return priorita;
    }

    public void setPriorita(Double priorita) {
        this.priorita = priorita;
    }

    public String getPopisCinnSPredmetem() {
        return popisCinnSPredmetem;
    }

    public void setPopisCinnSPredmetem(String popisCinnSPredmetem) {
        this.popisCinnSPredmetem = popisCinnSPredmetem;
    }

    public  static Sloupce getSLOUPCE() {
        return SLOUPCE;
    }
    
    public String toStr() {
        return String.format("%s %s",
                getNazev(),
                getPredmet() == null ? "bez předmětu" : getPredmet().getNazev());
    }

// ----------------------------- přepsané metody -------------------------------
    @Override
    public String toString() {
        return String.format("id %s cinnost(%s) predmet (%s) priorita %s",
                getId(),
                getNazev(),
                getPredmet() == null ? "bez předmětu" : getPredmet().toString(),
                getPredmet() == null ? "bez priority" : getPriorita().toString());
    }
}
