package cinnostiapredmety;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import utils.MnozPrvkuSNazv;
import utils.PrvekSNazvem;
import utils.ValuesFromToFileRow;
import utils.Vy;

public class CinnostiModel {

    public MnozPrvkuSNazv M_MATERIALY;
    public MnozPrvkuSNazv M_NARADI;
    public MnozPrvkuSNazv M_PREDMETY;

    public MnozPrvkuSNazv<Cinnost> M_CINNOSTI;
    public MnozPrvkuSNazv<PotrebneNaradi> M_POTREBNE_NARADI;
    public MnozPrvkuSNazv<PotrebnyMaterial> M_POTREBNE_MATERIALY;

    public static CinnostiModel instance;

    private CinnostiForm f;

    public CinnostiModel(String jmProjektu, CinnostiForm f) {
        this.f = f;
        ValuesFromToFileRow vff = new ValuesFromToFileRow(jmProjektu + ".txt");
        instance = this;

        M_NARADI = new MnozPrvkuSNazv("naradi", PrvekSNazvem.class);
        M_MATERIALY = new MnozPrvkuSNazv("materialy", PrvekSNazvem.class);

        M_PREDMETY = new MnozPrvkuSNazv(jmProjektu + "Predmety", PrvekSNazvem.class);

        M_POTREBNE_NARADI = new MnozPrvkuSNazv(jmProjektu + "Naradi", PotrebneNaradi.class);
        PotrebneNaradi.staticInit();
        M_POTREBNE_NARADI.setSloupce(PotrebneNaradi.getSLOUPCE());

        M_CINNOSTI = new MnozPrvkuSNazv<>(jmProjektu + "CinnAPredm", Cinnost.class);
        Cinnost.staticInit();
        M_CINNOSTI.setSloupce(Cinnost.getSLOUPCE());

        M_POTREBNE_MATERIALY = new MnozPrvkuSNazv(jmProjektu + "Materialy", PotrebnyMaterial.class);
        PotrebnyMaterial.staticInit();
        M_POTREBNE_MATERIALY.setSloupce(PotrebnyMaterial.getSLOUPCE());
    }

    public Cinnost getSelectedCinnost() {
        return M_CINNOSTI.dejPrvekId(f.getSelectedRowId());
    }

    public void nactiData() {
        M_PREDMETY.nactiZCsv();
        M_NARADI.nactiZCsv();
        M_CINNOSTI.nactiZCsv();
        M_POTREBNE_NARADI.nactiZCsv();
        M_MATERIALY.nactiZCsv();
        M_POTREBNE_MATERIALY.nactiZCsv();
    }

    public void vypisCinnKPredm(Comparator jak) {
        M_CINNOSTI.sPrvkyPodleId().sorted(jak).forEach(p -> Vy.w(p.toString()));
    }

    public void upDown(boolean up) {
        int i = f.t.getSelectedRow();
        if (i == -1) {
            return;
        }
        i = up ? (i - 1) : (i + 1);
        Cinnost cp = getSelectedCinnost();
        cp.setPriorita(cp.getPriorita() + (up ? -1.001 : 1.001));
        Double d = 1.0;
        List<Cinnost> l = (List) M_CINNOSTI.sPrvkyPodleId().sorted(Cinnost.DLE_PRIORITY).collect(Collectors.toList());
        for (Cinnost c : l) {
            c.setPriorita(d);
            d += 1;
        }
        f.initTableData();
        f.t.getSelectionModel().setSelectionInterval(i, i);
    }

    private PotrebnyMaterial isMaterialUCinn(PrvekSNazvem m) {
        return getPotrebnyMaterial().stream()
                .filter(p -> p.getMaterial().getId().equals(m.getId()))
                .findAny().orElse(null);
    }

    public void materialyPridatUbrat() {
        List<PrvekSNazvem> vm = f.getVybraneMaterialy();
        if (vm.isEmpty()) {
            return;
        }
        vm.stream().forEach(mat -> {
            PotrebnyMaterial pm = isMaterialUCinn(mat);
            if (pm != null) {
                if (0 == JOptionPane.showConfirmDialog(null, String.format("smazat materiál %s k činnosti", mat.getNazev()),
                        "Dotaz", JOptionPane.YES_NO_OPTION)) {
                    M_POTREBNE_MATERIALY.smazPrvekId(pm.getId());
                }
            } else {
                String pop = JOptionPane.showInputDialog(null, "k čemu materiál " + mat.getNazev(), "");
                pm = new PotrebnyMaterial();
                pm.setNazev(pop);
                pm.setId(M_POTREBNE_MATERIALY.getNewId());
                pm.setCinnost(getSelectedCinnost());
                pm.setMaterial(mat);
                M_POTREBNE_MATERIALY.pridejPrvek(pm);
            }
        });
        M_POTREBNE_MATERIALY.ulozDoCsv();
    }

    public PotrebneNaradi isNaradiUCinn(PrvekSNazvem n) {
        return getPotrebneNaradi().stream()
                .filter(pn -> pn.getNaradi().getId().equals(n.getId()))
                .findAny().orElse(null);
    }

    public void naradiKCinnostiPridatUbrat() {
        if (f.getVybraneNaradi().size() == 0) {
            return;
        }
        Cinnost c = getSelectedCinnost();
        f.getVybraneNaradi().stream().forEach(n -> {
            PotrebneNaradi pna = isNaradiUCinn(n);
            if (pna != null) {
                if (0 == JOptionPane.showConfirmDialog(null, String.format("smazat nářadí %s k činnosti", n.getNazev()),
                        "Dotaz", JOptionPane.YES_NO_OPTION)) {
                    M_POTREBNE_NARADI.smazPrvekId(pna.getId());
                }
            } else {
                String popis = JOptionPane.showInputDialog(null, "činnost s nářadím " + n.getNazev(), "");
                pna = new PotrebneNaradi();
                pna.setNazev(popis);
                pna.setId(M_POTREBNE_NARADI.getNewId());
                pna.setCinnost(c);
                pna.setNaradi(n);
                M_POTREBNE_NARADI.pridejPrvek(pna);
            }
        });
        M_POTREBNE_NARADI.ulozDoCsv();
    }

    public List<PotrebneNaradi> getPotrebneNaradi() {
        return getPotrebneNaradi(getSelectedCinnost());
    }

    public List<PotrebneNaradi> getPotrebneNaradi(Cinnost cinnPredm) {
        return (List<PotrebneNaradi>) M_POTREBNE_NARADI.sPrvkyPodleId()
                .filter(p -> p.getCinnost() != null && ((PotrebneNaradi) p).getCinnost().getId().equals(cinnPredm.getId()))
                .collect(Collectors.toList());
    }

    public List<PotrebnyMaterial> getPotrebnyMaterial() {
        return getPotrebnyMaterial(getSelectedCinnost());
    }

    public List<PotrebnyMaterial> getPotrebnyMaterial(Cinnost c) {
        return (List<PotrebnyMaterial>) M_POTREBNE_MATERIALY.sPrvkyPodleId()
                .filter(p -> ((PotrebnyMaterial) p).getCinnost().getId().equals(c.getId()))
                .collect(Collectors.toList());
    }

    public void smazNaradi() {
        PrvekSNazvem n = f.getSelectedNaradi();
        if (n == null) {
            return;
        }
        boolean pouzit = M_POTREBNE_NARADI
                .sPrvkyPodleId()
                .filter(pn -> pn.getNaradi().equals(n))
                .findAny().orElse(null) != null;
        if (pouzit) {
            Vy.wf("nářadí je v tomto projektu použito");
            int s = JOptionPane.showConfirmDialog(null, "nářadí je v tomto projektu", "Nelze", JOptionPane.OK_OPTION);
        } else {
            int s = JOptionPane.showConfirmDialog(null, "smazat nářadí (může být v jiném projektu", "Dotaz", JOptionPane.YES_NO_OPTION);
            if (s == 0) {
                M_NARADI.smazPrvekId(n.getId());
            }
        }
    }

    private Double getMaxPriorita() {
        Cinnost cp = M_CINNOSTI.sPrvkyPodleId().max(Cinnost.DLE_PRIORITY).orElse(null);
        return cp == null ? 1.0 : cp.getPriorita();
    }

    public void novaCinnost() {
        Cinnost c = new Cinnost();
        c.setId(M_CINNOSTI.getNewId());
        c.setNazev("nová činnost");
        c.setPredmet(f.getSelectedPredmet());
        c.setPriorita(getMaxPriorita() + 1.0);
        M_CINNOSTI.pridejPrvek(c);
        M_CINNOSTI.ulozDoCsv();
        f.initTableData();
        int i = M_CINNOSTI.dejRozmer() - 1;
        f.t.getSelectionModel().setSelectionInterval(i, i);
    }

    public void smazCinnost() {
        if (getSelectedCinnost() == null) {
            return;
        }
        smazVztahyKCinnosti();
        M_CINNOSTI.smazPrvekId(getSelectedCinnost().getId());
        M_CINNOSTI.ulozDoCsv();
        f.initTableData();
    }

    private void smazVztahyKCinnosti() {
        getPotrebneNaradi().stream().forEach(p -> M_POTREBNE_NARADI.smazPrvekId(p.getId()));
        getPotrebnyMaterial().stream().forEach(p -> M_POTREBNE_MATERIALY.smazPrvekId(p.getId()));
    }

    public void novyPredmet() {
        String popis = JOptionPane.showInputDialog(null, "nový předmět", "");
        PrvekSNazvem predmet = new PrvekSNazvem();
        predmet.setId(M_NARADI.getNewId());
        predmet.setNazev(popis);
        M_PREDMETY.pridejPrvek(predmet);
        M_PREDMETY.ulozDoCsv();
    }

    public void smazPredmet() {
        PrvekSNazvem predmet = f.getSelectedPredmet();
        if (predmet == null) {
            return;
        }
        boolean pouzit = M_CINNOSTI
                .sPrvkyPodleId()
                .filter(cm -> cm.getPredmet().equals(predmet))
                .findAny().orElse(null) != null;
        if (pouzit) {
            JOptionPane.showConfirmDialog(null, "předmět je v tomto projektu použit", "Nelze", JOptionPane.OK_OPTION);
        } else {
            int s = JOptionPane.showConfirmDialog(null, "smazat předmět?", "Dotaz", JOptionPane.YES_NO_OPTION);
            if (s == 0) {
                M_PREDMETY.smazPrvekId(predmet.getId());
                M_PREDMETY.ulozDoCsv();
            }
        }
    }

    public void noveNaradi() {
        String popis = JOptionPane.showInputDialog(null, "nové nářadí", "");
        PrvekSNazvem naradi = new PrvekSNazvem();
        naradi.setId(M_NARADI.getNewId());
        naradi.setNazev(popis);
        M_NARADI.pridejPrvek(naradi);
        M_NARADI.ulozDoCsv();
    }

    public void novyMaterial() {
        String popis = JOptionPane.showInputDialog(null, "nový materiál", "");
        PrvekSNazvem mat = new PrvekSNazvem();
        mat.setId(M_MATERIALY.getNewId());
        mat.setNazev(popis);
        M_MATERIALY.pridejPrvek(mat);
        M_MATERIALY.ulozDoCsv();
    }

    public void smazMaterial() {
        PrvekSNazvem m = f.getSelectedMaterial();
        boolean pouzit = M_POTREBNE_MATERIALY.sPrvkyPodleId()
                .filter(p -> p.getMaterial().getId().equals(m.getId()))
                .findAny()
                .orElse(null) != null;

        if (pouzit) {
            JOptionPane.showConfirmDialog(null, "materiál je v tomto projektu použit", "Nelze", JOptionPane.OK_CANCEL_OPTION);
        } else {
            int s = JOptionPane.showConfirmDialog(null, "smazat materiál? (může být použit v jiném projektu)", "Dotaz", JOptionPane.YES_NO_OPTION);
            if (s == 0) {
                M_MATERIALY.smazPrvekId(m.getId());
                M_MATERIALY.ulozDoCsv();
            }
        }
    }

}
