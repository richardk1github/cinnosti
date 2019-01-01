package cinnostiapredmety;

import utils.PrvekSNazvem;
import utils.Sloupce;
import utils.Sloupec;

public class PotrebnyMaterial extends PrvekSNazvem {

    private static final Sloupce SLOUPCE = new Sloupce(PotrebnyMaterial.class);

    private Cinnost cinnost;
    private PrvekSNazvem material;
    private static CinnostiModel model = CinnostiModel.instance;
    
    public PotrebnyMaterial() {
    }

    public static void staticInit() {
        SLOUPCE.addSloupec(new Sloupec<>(PrvekSNazvem.ID, "ID činnosti s předmětem", Integer.class, 10));
        SLOUPCE.addSloupec(new Sloupec<>(PrvekSNazvem.NAZEV, "název", String.class, 20));
        SLOUPCE.addSloupec(new Sloupec<PotrebnyMaterial>("cinnost", "Činnost", Cinnost.class) {
            @Override
            public void fromCsv(PotrebnyMaterial ofObject, String csv) {
                ofObject.setCinnost(model.M_CINNOSTI.dejPrvekId(Integer.valueOf(csv)));
            }
        });
        SLOUPCE.addSloupec(new Sloupec<>("material", "Materiál", model.M_MATERIALY));
    }

    public Cinnost getCinnost() {
        return cinnost;
    }

    public void setCinnost(Cinnost cinnost) {
        this.cinnost = cinnost;
    }

    public PrvekSNazvem getMaterial() {
        return material;
    }

    public void setMaterial(PrvekSNazvem material) {
        this.material = material;
    }

    public static Sloupce getSLOUPCE() {
        return SLOUPCE;
    }

    @Override
    public String toString() {
        return String.format("[%s]%s činnost(%s) nářadí(%s)",
                getId(), getNazev(),
                getCinnost() == null ? "???" : getCinnost().toString(),
                getMaterial() == null ? "???" : getMaterial().toString());
    }

    public String toStr() {
        return String.format("%s (%s)", getMaterial().toString(), getNazev());
    }

}
