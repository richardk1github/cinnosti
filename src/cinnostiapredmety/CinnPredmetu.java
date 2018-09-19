package cinnostiapredmety;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import utils.MnozPrvkuSNazv;
import utils.PrvekSNazvem;
import utils.Sloupce;
import utils.Sloupec;
import utils.ValuesFromToFileRow;
import utils.Vy;

public class CinnPredmetu extends PrvekSNazvem {

    public static final String ID_CINNOSTI = "idCinnosti";
    public static final String ID_PREDMETU = "idPredmetu";
    public static final String PRIORITA = "priorita";

    private static MnozPrvkuSNazv mnozinaCinnosti;
    private static MnozPrvkuSNazv mnozinaPredmetu;
    private static MnozPrvkuSNazv<CinnPredmetu> mnozCinnPredmetu;
    private static DefaultTableModel dtaModel = new DefaultTableModel();
    private static JTable t = new JTable();
    private static Order order = Order.OrdPriorita;
    private static Integer selectedId;

    public static final Comparator<CinnPredmetu> DLE_PRIORITY = (CinnPredmetu o1, CinnPredmetu o2) -> o1.getPriorita().compareTo(o2.getPriorita());
    public static final Comparator<CinnPredmetu> DLE_PREDMETU = (CinnPredmetu o1, CinnPredmetu o2) -> {
        int i = o1.getPredmet().getNazev().compareTo(o2.getPredmet().getNazev());
        return i == 0 ? o1.getPriorita().compareTo(o2.getPriorita()) : i;
    };
    public static final Comparator<CinnPredmetu> DLE_CINNOSTI = (CinnPredmetu o1, CinnPredmetu o2) -> {
        int i = o1.getCinnost().getNazev().compareTo(o2.getCinnost().getNazev());
        return i == 0 ? o1.getPriorita().compareTo(o2.getPriorita()) : i;
    };

    public static final Sloupce SLOUPCE = new Sloupce(CinnPredmetu.class);

    private PrvekSNazvem cinnost;
    private PrvekSNazvem predmet;
    private Double priorita;

    static {
        init();
    }

    public CinnPredmetu() {
    }

    private static void init() {
        SLOUPCE.addSloupec(new Sloupec<>(PrvekSNazvem.ID, "ID činnosti s předmětem", Integer.class, 10));
        SLOUPCE.addSloupec(new Sloupec<>(PrvekSNazvem.NAZEV, "název", String.class, 20));
        SLOUPCE.addSloupec(new Sloupec<CinnPredmetu>("cinnost", "Činnost", PrvekSNazvem.class) {
            @Override
            public void fromCsv(CinnPredmetu ofObject, String csv) {
                ofObject.setCinnost(((PrvekSNazvem) mnozinaCinnosti.dejPrvekId(Integer.valueOf(csv))));
            }
        });
        SLOUPCE.addSloupec(new Sloupec<CinnPredmetu>("predmet", "Předmět", PrvekSNazvem.class) {
            @Override
            public void fromCsv(CinnPredmetu ofObject, String csv) {
                ofObject.setPredmet(((PrvekSNazvem) mnozinaPredmetu.dejPrvekId(Integer.valueOf(csv))));
            }
        });
        SLOUPCE.addSloupec(new Sloupec<>(PRIORITA, "priorita", Double.class, 10));

        Action smaz = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (selectedId == null) {
                    return;
                }
                CinnPredmetu.getCinnPredmetu().smazPrvekId(selectedId);
                initData();
            }
        };
        t.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "smaz");
        t.getActionMap().put("smaz", smaz);
    }

    public List<String> getColNames() {
        return SLOUPCE.getNazvySloupcu();
    }

    public PrvekSNazvem getCinnost() {
        return cinnost;
    }

    public void setCinnost(PrvekSNazvem cinnost) {
        this.cinnost = cinnost;
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

    public void vypisPredmet() {
        System.out.println(toString());
    }

    public static List<PrvekSNazvem> getCinnosti() {
        return (List<PrvekSNazvem>) mnozinaCinnosti.sPrvekSNazvem().sorted(MnozPrvkuSNazv.DLE_NAZVU).collect(Collectors.toList());
    }

    public static MnozPrvkuSNazv getMnozPredmety() {
        return mnozinaPredmetu;
    }

    public static MnozPrvkuSNazv getMnozCinnosti() {
        return mnozinaCinnosti;
    }

    private static void selectAgain() {
        if (selectedId != null) {
            selectRow(selectedId);
        }
    }

    private static void selectRow(int id) {
        for (int row = 0; row < t.getModel().getRowCount(); row++) {
            if (t.getModel().getValueAt(row, 0).equals(Integer.valueOf(id))) {
                t.getSelectionModel().setSelectionInterval(row, row);
                break;
            }
        }
    }

    public static List<PrvekSNazvem> getPredmety() {
        return (List<PrvekSNazvem>) mnozinaPredmetu.sPrvekSNazvem().sorted(MnozPrvkuSNazv.DLE_NAZVU).collect(Collectors.toList());
    }

    @Override
    public String[] readObjFromCsv(String line) {
        String[] radek = SLOUPCE.nactiObjZCsv(this, line);
        return radek;
    }

    @Override
    public String writeObjToCsv() {
        return SLOUPCE.zapisObjDoCsv(this);
    }

    public static void novaPriorita() {
        Iterator<CinnPredmetu> it = mnozCinnPredmetu.sPrvekSNazvem().sorted(DLE_PRIORITY).iterator();
        int i = 1;
        while (it.hasNext()) {
            it.next().setPriorita(Double.valueOf(String.valueOf(Integer.valueOf(i))));
            i++;
        }
    }

    public void upDown(boolean up) {
        int i = t.getSelectedRow();
        i = up ? (i - 1) : (i + 1);
        setPriorita(getPriorita() + (up ? -1.001 : 1.001));
        Double d = 1.0;
        List<CinnPredmetu> l = (List) CinnPredmetu.getCinnPredmetu().sPrvekSNazvem().sorted(DLE_PRIORITY).collect(Collectors.toList());
        for (CinnPredmetu c : l) {
            c.setPriorita(d);
            d += 1;
        }
        initData();
        t.getSelectionModel().setSelectionInterval(i, i);
    }

    public static void vypisCinnKPredm(Comparator jak) {
        mnozCinnPredmetu.sPrvekSNazvem().sorted(jak).forEach(p -> Vy.w(p.toString()));
    }

    public static MnozPrvkuSNazv getCinnPredmetu() {
        return mnozCinnPredmetu;
    }

// ----------------------------- statické metody -------------------------------
// ----------------------------- přepsané metody -------------------------------
    @Override
    public String toString() {
        return String.format("id %s cinnost(%s) predmet (%s) priorita %s",
                getId().toString(),
                //                getNazev(),
                getCinnost() == null ? "bez činnosti" : getCinnost().toString(),
                getPredmet() == null ? "bez předmětu" : getPredmet().toString(),
                getPredmet() == null ? "bez priority" : getPriorita().toString());

    }

    public String toStr() {
        return String.format("%s %s",
                getCinnost() == null ? "bez činnosti" : getCinnost().getNazev(),
                getPredmet() == null ? "bez předmětu" : getPredmet().getNazev());
    }

    private static Double getMaxPriorita() {
        CinnPredmetu cp = (CinnPredmetu) CinnPredmetu.getCinnPredmetu().sPrvekSNazvem().max(DLE_PRIORITY).orElse(null);
        return cp == null ? 1.0 : cp.getPriorita();
    }

    private static void initData() {
        String[] nadpisy = new String[CinnPredmetu.SLOUPCE.dejRozmer()];
        Object dataPole[][] = new Object[CinnPredmetu.getCinnPredmetu().dejRozmer()][CinnPredmetu.SLOUPCE.dejRozmer()];
        int i = 0;
        Comparator<CinnPredmetu> comp = null;
        switch (order) {
            case OrdPredmet:
                comp = DLE_PREDMETU;
                break;
            case OrdPriorita:
                comp = DLE_PRIORITY;
                break;
            case OrdCinnost:
                comp = DLE_CINNOSTI;
                break;
        }
        Iterator<CinnPredmetu> it = CinnPredmetu.getCinnPredmetu().sPrvekSNazvem().sorted(comp).iterator();
        while (it.hasNext()) {
            CinnPredmetu c = it.next();
            Vy.w(c.toStr());
            for (int j = 0; j < CinnPredmetu.SLOUPCE.dejRozmer(); j++) {
                dataPole[i][j] = CinnPredmetu.SLOUPCE.getValue(c, CinnPredmetu.SLOUPCE.getSloupec(j));
            }
            i++;
        }

        dtaModel.getDataVector().clear();
        dtaModel.setDataVector(dataPole, CinnPredmetu.SLOUPCE.getNazvySloupcu().toArray(nadpisy));
        t.setModel(dtaModel);
        TableColumnModel tcm = t.getColumnModel();
        int slo = 0;
        Enumeration<TableColumn> e = tcm.getColumns();
        while (e.hasMoreElements()) {
            TableColumn tc = e.nextElement();
            tc.setPreferredWidth(CinnPredmetu.SLOUPCE.getSloupec(slo).getSize());
            slo++;
        }
    }
    // -----------------------------------------------------------------------------

    public static void main(String[] args) {
        ValuesFromToFileRow vff = new ValuesFromToFileRow(args[0]);
        String[] r = vff.getValues();
        Arrays.asList(r).forEach(p->Vy.w(p.toString()));
        
        mnozinaCinnosti = new MnozPrvkuSNazv(r[0], PrvekSNazvem.class);
        mnozinaCinnosti.nactiZCsv();
        mnozinaPredmetu = new MnozPrvkuSNazv(r[1], PrvekSNazvem.class);
        mnozinaPredmetu.nactiZCsv();
        mnozCinnPredmetu = new MnozPrvkuSNazv<CinnPredmetu>(r[2], CinnPredmetu.class);
        mnozCinnPredmetu.nactiZCsv();

        JFrame frame = new JFrame(r[3]);
        JPanel jp = new JPanel();
        JScrollPane jScrollPane1 = new JScrollPane();

        initData();
        jScrollPane1.setViewportView(t);

        jp.add(jScrollPane1);
        PanelX jp2 = new PanelX(t);

        t.getSelectionModel().addListSelectionListener(jp2);

        jp.add(jp2);
        frame.getContentPane().add(jp);
        frame.pack();

        frame.setSize(850, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
    }

    public static class PanelX extends JPanel implements ActionListener, ListSelectionListener {

        private JComboBox cbCinnost = new JComboBox(CinnPredmetu.getCinnosti().toArray());
        private JComboBox cbPredmet = new JComboBox(CinnPredmetu.getPredmety().toArray());
        private JPanel pnlTlac = new JPanel();

        public PanelX(JTable ta) {
            initComp();
        }

        private void initComp() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            add(cbPredmet);
            add(cbCinnost);

            tlacitko(Akce.Nahoru);
            tlacitko(Akce.Dolu);
            tlacitko(Akce.Priorita);
            tlacitko(Akce.Predmet);
            tlacitko(Akce.Cinnost);
            tlacitko(Akce.Pridat);
            tlacitko(Akce.PridatCinnost);
            tlacitko(Akce.PridatPredmet);
            tlacitko(Akce.Ulozit);

            add(pnlTlac);
        }

        private void tlacitko(Akce a) {
            JButton tl = new JButton(a.name());
            pnlTlac.add(tl);
            tl.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (Akce.valueOf(e.getActionCommand())) {
                case Nahoru: {
                    Vy.w("nahoru");
                    if (selectedId == null) {
                        return;
                    }
                    ((CinnPredmetu) CinnPredmetu.getCinnPredmetu().dejPrvekId(selectedId)).upDown(true);
                    return;
                }
                case Dolu: {
                    if (selectedId == null) {
                        return;
                    }
                    ((CinnPredmetu) CinnPredmetu.getCinnPredmetu().dejPrvekId(selectedId)).upDown(false);
                    return;
                }
                case Priorita: {
                    order = Order.OrdPriorita;
                    initData();
                    selectAgain();
                    break;
                }
                case Predmet: {
                    order = Order.OrdPredmet;
                    initData();
                    selectAgain();
                    break;
                }
                case Cinnost: {
                    order = Order.OrdCinnost;
                    int i = t.getSelectedRow();
                    initData();
                    selectAgain();
                    break;
                }
                case Pridat: {
                    if (cbPredmet.getSelectedItem() == null || cbCinnost.getSelectedItem() == null) {
                        return;
                    }
                    CinnPredmetu c = new CinnPredmetu();
                    c.setId(CinnPredmetu.getCinnPredmetu().getNewId());
                    c.setNazev("a");
                    c.setPredmet(((PrvekSNazvem) cbPredmet.getSelectedItem()));
                    c.setCinnost(((PrvekSNazvem) cbCinnost.getSelectedItem()));
                    c.setPriorita(getMaxPriorita() + 1.0);
                    CinnPredmetu.getCinnPredmetu().pridejPrvek(c);
                    initData();
                    int i = CinnPredmetu.getCinnPredmetu().dejRozmer() - 1;
                    t.getSelectionModel().setSelectionInterval(i, i);
                    break;
                }
                case PridatPredmet:
                    String a = JOptionPane.showInputDialog(null, "nový předmět", "");
                    PrvekSNazvem p = new PrvekSNazvem();
                    p.setId(CinnPredmetu.getMnozPredmety().getNewId());
                    p.setNazev(a);
                    CinnPredmetu.getMnozPredmety().pridejPrvek(p);
                    cbPredmet.setModel(new DefaultComboBoxModel(CinnPredmetu.getPredmety().toArray()));
                    break;

                case PridatCinnost:
                    String b = JOptionPane.showInputDialog(null, "nová činnost", "");
                    PrvekSNazvem q = new PrvekSNazvem();
                    q.setId(CinnPredmetu.getMnozCinnosti().getNewId());
                    q.setNazev(b);
                    CinnPredmetu.getMnozCinnosti().pridejPrvek(q);
                    cbCinnost.setModel(new DefaultComboBoxModel(CinnPredmetu.getCinnosti().toArray()));
                    break;
                case Ulozit:
                    CinnPredmetu.getCinnPredmetu().ulozDoCsv();
                    CinnPredmetu.getMnozCinnosti().ulozDoCsv();
                    CinnPredmetu.getMnozPredmety().ulozDoCsv();
                    break;
            }

        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                if (t == null) {
                    return;
                }
                if (t.getSelectedRow() >= 0) {
                    selectedId = (Integer) t.getValueAt(t.getSelectedRow(), 0);
                }
                /*else {
                    selectedId = null;
                }*/

            }
        }
    }

    private enum Akce {
        Cinnost,
        Dolu,
        Nahoru,
        Predmet,
        Pridat,
        PridatPredmet,
        PridatCinnost,
        Priorita,
        Ulozit;
    }

    private enum Order {
        OrdPriorita, OrdPredmet, OrdCinnost;
    }
}
