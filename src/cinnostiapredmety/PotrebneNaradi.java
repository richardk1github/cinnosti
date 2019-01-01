package cinnostiapredmety;

import utils.PrvekSNazvem;
import utils.Sloupce;
import utils.Sloupec;

public class PotrebneNaradi extends PrvekSNazvem {

    private static final Sloupce SLOUPCE = new Sloupce(PotrebneNaradi.class);

    private Cinnost cinnost;
    private PrvekSNazvem naradi;

    public PotrebneNaradi() {
    }

    public static void staticInit() {

        SLOUPCE.addSloupec(new Sloupec<>(PrvekSNazvem.ID, "ID činnosti s předmětem a nářadí", Integer.class, 10));
        SLOUPCE.addSloupec(new Sloupec<>(PrvekSNazvem.NAZEV, "název", String.class, 20));
        SLOUPCE.addSloupec(new Sloupec<PotrebneNaradi>("cinnost", "Činnost", Cinnost.class) {
            @Override
            public void fromCsv(PotrebneNaradi ofObject, String csv) {
                ofObject.setCinnost(CinnostiModel.instance.M_CINNOSTI.dejPrvekId(Integer.valueOf(csv)));
            }
        });
        SLOUPCE.addSloupec(new Sloupec<>("naradi", "Nářadí", CinnostiModel.instance.M_NARADI));
    }

    public Cinnost getCinnost() {
        return cinnost;
    }

    public void setCinnost(Cinnost cinnost) {
        this.cinnost = cinnost;
    }

    public PrvekSNazvem getNaradi() {
        return naradi;
    }

    public void setNaradi(PrvekSNazvem naradi) {
        this.naradi = naradi;
    }

    public static Sloupce getSLOUPCE() {
        return SLOUPCE;
    }

    @Override
    public String toString() {
        return String.format("[%s]%s činnost(%s) nářadí(%s)",
                getId(), getNazev(),
                getCinnost() == null ? "bez činnosti" : getCinnost().toString(),
                getNaradi() == null ? "bez předmětu" : getNaradi().toString());
    }

    public String toStr() {
        return String.format("%s (%s)",
                getNaradi() == null ? "bez předmětu" : getNaradi().toString(),
                getNazev());
    }

}
