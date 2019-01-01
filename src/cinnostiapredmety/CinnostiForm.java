package cinnostiapredmety;

import utils.InsertDeleteProvider;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import utils.MnozPrvkuSNazv;
import utils.PrvekSNazvem;
import utils.Sloupce;
import utils.ValuesFromToFileRow;
import utils.Vy;

public class CinnostiForm extends PrvekSNazvem {

    public final String PRIORITA = "priorita";
    public final String POPIS = "popisCinnSPredmetem";

    private DefaultTableModel dtaModel = new DefaultTableModel();
    private Integer selectedCinnostId;

    public JTable t = new JTable() {
        @Override
        public void tableChanged(TableModelEvent e) {
            super.tableChanged(e);
            int modelColumn = e.getColumn();
            if (modelColumn > 0 && t != null) {
                t.getSelectedRow();
                if (Cinnost.getSLOUPCE().getSloupec(modelColumn).getNazevSloupce().equals(POPIS)) {
                    String s = (String) t.getValueAt(t.getSelectedRow(), modelColumn);
                    model.getSelectedCinnost().setPopisCinnSPredmetem(s);
                    model.M_CINNOSTI.ulozDoCsv();
                }
                if (Cinnost.getSLOUPCE().getSloupec(modelColumn).getNazevSloupce().equals(PrvekSNazvem.NAZEV)) {
                    String s = (String) t.getValueAt(t.getSelectedRow(), modelColumn);
                    model.getSelectedCinnost().setNazev(s);
                    model.M_CINNOSTI.ulozDoCsv();
                }
            }
        }
    };
    private JList naradiList = new JList();
    private JList materialyList = new JList();
    private JComboBox cbPredmet = new JComboBox();
    private List<PrvekSNazvem> vybranaNaradi = new ArrayList<>();
    private List<PrvekSNazvem> vybraneMaterialy = new ArrayList<>();
    private Order order = Order.OrdPriorita;
    private DefaultListModel naradiListModel = new DefaultListModel();
    private DefaultListModel materialyListModel = new DefaultListModel();
    private PanelX jp2;
        
    private CinnostiModel model;

    private void selectAgain() {
        if (selectedCinnostId != null) {
            selectRow(selectedCinnostId);
        }
    }

    private void selectRow(int id) {
        for (int row = 0; row < t.getModel().getRowCount(); row++) {
            if (t.getModel().getValueAt(row, 0).equals(Integer.valueOf(id))) {
                t.getSelectionModel().setSelectionInterval(row, row);
                break;
            }
        }
    }

    public int getSelectedRowId() {
        return (Integer) t.getValueAt(t.getSelectedRow(), 0);
    }

    public PrvekSNazvem getSelectedNaradi() {
        return (PrvekSNazvem) naradiList.getSelectedValue();
    }

    public PrvekSNazvem getSelectedPredmet() {
        return (PrvekSNazvem) cbPredmet.getSelectedItem();
    }

    public PrvekSNazvem getSelectedMaterial() {
        return (PrvekSNazvem) materialyList.getSelectedValue();
    }

    public void setCbPredmetModel() {
        cbPredmet.setModel(new DefaultComboBoxModel(model.M_PREDMETY.sPrvkyPodleNazvu().toArray()));
    }

    public List<PrvekSNazvem> getVybraneMaterialy() {
        return vybraneMaterialy;
    }

    public List<PrvekSNazvem> getVybraneNaradi() {
        return vybranaNaradi;
    }

    public List<PrvekSNazvem> getPredmety() {
        return (List<PrvekSNazvem>) model.M_PREDMETY.sPrvkyPodleNazvu().collect(Collectors.toList());
    }

    public List<PrvekSNazvem> getNaradi() {
        return (List<PrvekSNazvem>) model.M_NARADI.sPrvkyPodleNazvu().collect(Collectors.toList());
    }

    public List<PrvekSNazvem> getMaterialy() {
        return (List<PrvekSNazvem>) model.M_MATERIALY.sPrvkyPodleNazvu().collect(Collectors.toList());
    }

    private void initNaradiData() {
        naradiListModel.clear();
        model.M_NARADI.dejPrvky().stream().sorted(MnozPrvkuSNazv.DLE_NAZVU).forEach(p -> naradiListModel.addElement(p));
        naradiList.setModel(naradiListModel);
    }

    private void initMaterialyData() {
        materialyListModel.clear();
        model.M_MATERIALY.dejPrvky().stream().sorted(MnozPrvkuSNazv.DLE_NAZVU).forEach(p -> materialyListModel.addElement(p));
        materialyList.setModel(materialyListModel);
    }

    private void initPotrebneNaradiData() {
        initTableData();
    }

    public void setOrder(Order o) {
        order = o;
        initTableData();
        selectAgain();
    }

    public void initTableData() {
        Sloupce sloupce = Cinnost.getSLOUPCE();
        int sloupcu = sloupce.dejRozmer();
        String[] nadpisy = new String[sloupcu];
        Object dataPole[][] = new Object[model.M_CINNOSTI.dejRozmer()][sloupcu];
        int i = 0;
        Comparator<Cinnost> comp = null;
        switch (order) {
            case OrdPredmet:
                comp = Cinnost.DLE_PREDMETU;
                break;
            case OrdPriorita:
                comp = Cinnost.DLE_PRIORITY;
                break;
            case OrdCinnost:
                comp = Cinnost.DLE_CINNOSTI;
                break;
        }
        Iterator<Cinnost> it = model.M_CINNOSTI.sPrvkyPodleId().sorted(comp).iterator();
        while (it.hasNext()) {
            Cinnost c = it.next();
            for (int j = 0; j < sloupcu; j++) {
                dataPole[i][j] = sloupce.getValue(c, sloupce.getSloupec(j));
            }
            i++;
        }

        dtaModel.getDataVector().clear();
        dtaModel.setDataVector(dataPole, sloupce.getPopisySloupcu().toArray(nadpisy));
        t.setModel(dtaModel);
        TableColumnModel tcm = t.getColumnModel();
        int slo = 0;
        Enumeration<TableColumn> e = tcm.getColumns();
        while (e.hasMoreElements()) {
            TableColumn tc = e.nextElement();
            tc.setPreferredWidth(sloupce.getSloupec(slo).getSize());
            slo++;
        }
    }
    // -----------------------------------------------------------------------------

    public static void main(String[] args) {
        ValuesFromToFileRow vff = new ValuesFromToFileRow(args[0] + ".txt");
        String[] jmenaSouboru = vff.getValues();

        CinnostiForm f = new CinnostiForm();
        f.model = new CinnostiModel(args[0], f);
        f.model.nactiData();

        JFrame frame = new JFrame(jmenaSouboru[0]);
        JPanel jp = new JPanel(new BorderLayout());
        JPanel pnlCinnosti = new JPanel(new BorderLayout());
        JPanel pnlNaradi = new JPanel(new BorderLayout());
        JPanel pnlMaterialy = new JPanel(new BorderLayout());
        JScrollPane scpCinnosti = new JScrollPane();
        JScrollPane scpNaradi = new JScrollPane();
        JScrollPane scpMaterialy = new JScrollPane();

        f.initTableData();
        f.initNaradiData();
        f.initMaterialyData();
        f.initPotrebneNaradiData();

        scpCinnosti.setViewportView(f.t);
        pnlCinnosti.add(scpCinnosti);
        jp.add(pnlCinnosti, BorderLayout.WEST);

        scpNaradi.setViewportView(f.naradiList);
        pnlNaradi.add(scpNaradi);
        jp.add(pnlNaradi, BorderLayout.EAST);

        scpMaterialy.setViewportView(f.materialyList);
        pnlMaterialy.add(scpMaterialy);
        jp.add(pnlMaterialy, BorderLayout.CENTER);

        f.jp2 = f.new PanelX();
        f.t.getSelectionModel().addListSelectionListener(f.jp2);

        f.naradiList.getSelectionModel().addListSelectionListener(f.jp2);
        f.materialyList.getSelectionModel().addListSelectionListener(f.jp2);
        jp.add(f.jp2, BorderLayout.SOUTH);

        frame.getContentPane().add(jp);
        frame.pack();

        frame.setSize(850, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
    }

    private class PanelX extends JPanel implements ActionListener, ListSelectionListener {

        private JPanel pnlTlac = new JPanel();
        private JTextArea lblNaradi = new JTextArea("nářadí k činnosti");

        public PanelX() {
            initComp();
        }

        private void initComp() {
            setCbPredmetModel();

            new InsertDeleteProvider(t, "smazCinnost", "novaCinnost") {
                @Override
                public void fireDeleteAction() {
                    model.smazCinnost();
                }

                @Override
                public void fireInsertAction() {
                    if (cbPredmet.getSelectedItem() == null) {
                        return;
                    }
                    model.novaCinnost();
                }
            };

            new InsertDeleteProvider(materialyList, "smazMaterial", "novyMaterial") {
                @Override
                public void fireDeleteAction() {
                    model.smazMaterial();
                    initMaterialyData();
                }

                @Override
                public void fireInsertAction() {
                    model.novyMaterial();
                    initMaterialyData();
                }
            };

            new InsertDeleteProvider(naradiList, "smazNaradi", "noveNaradi") {
                @Override
                public void fireDeleteAction() {
                    model.smazNaradi();
                    initNaradiData();
                }

                @Override
                public void fireInsertAction() {
                    model.noveNaradi();
                    initNaradiData();
                }
            };

            new InsertDeleteProvider(cbPredmet, "smazPredmet", "novyPredmet") {
                @Override
                public void fireDeleteAction() {
                    model.smazPredmet();
                    setCbPredmetModel();
                }

                @Override
                public void fireInsertAction() {
                    model.novyPredmet();
                    setCbPredmetModel();
                }
            };

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            add(lblNaradi);
            add(cbPredmet);

            tlacitko(Akce.Nahoru);
            tlacitko(Akce.Dolu);
            tlacitko(Akce.Priorita);
            tlacitko(Akce.Predmet);
            tlacitko(Akce.Cinnost);
            tlacitko(Akce.MaterialPridatUbrat);
            tlacitko(Akce.NaradiPridatUbrat);
            tlacitko(Akce.Vypsat);

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
                case Nahoru:
                    model.upDown(true);
                    break;

                case Dolu:
                    model.upDown(false);
                    break;

                case Priorita:
                    setOrder(Order.OrdPriorita);
                    break;

                case Predmet:
                    setOrder(Order.OrdPredmet);
                    break;

                case Cinnost:
                    setOrder(Order.OrdCinnost);    // řazení podle názvu činnosti
                    break;

                case MaterialPridatUbrat:
                    model.materialyPridatUbrat();
                    break;

                case NaradiPridatUbrat:
                    model.naradiKCinnostiPridatUbrat();
                    break;

                case Vypsat:
                    Sloupce s = Cinnost.getSLOUPCE();
                    model.M_CINNOSTI
                            .sPrvkyPodleId()
                            .sorted(Cinnost.DLE_PRIORITY)
                            .forEach(cp -> {
                                Vy.w(s.toStr(cp));
                                model.getPotrebneNaradi(cp)
                                        .stream()
                                        .forEach(pn -> {
                                            Vy.w("    - " + pn.toStr());
                                        });
                                model.getPotrebnyMaterial(cp)
                                        .stream()
                                        .forEach(pm -> {
                                            Vy.w("    - " + pm.toStr());
                                        });
                            });
                    break;
            }

        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }

            if (e.getSource().equals(naradiList.getSelectionModel())) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                vybranaNaradi.clear();
                naradiList.getSelectedValuesList().stream().forEach(p -> vybranaNaradi.add((PrvekSNazvem) p));

            } else if (e.getSource().equals(materialyList.getSelectionModel())) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                vybraneMaterialy.clear();
                materialyList.getSelectedValuesList().stream().forEach(p -> vybraneMaterialy.add((PrvekSNazvem) p));

            } else if (e.getSource().equals(t.getSelectionModel())) {
                if (t.getSelectedRow() >= 0) {
                    selectedCinnostId = model.getSelectedCinnost().getId();
                    lblNaradi.setText("");
                    model.getPotrebneNaradi()
                            .stream()
                            .forEach(p -> lblNaradi.setText(p.getNaradi().toString() + " (" + p.getNazev() + ")\n"
                            + lblNaradi.getText()));
                    model.getPotrebnyMaterial()
                            .stream()
                            .forEach(p -> lblNaradi.setText(p.getMaterial().toString() + " (" + p.getNazev() + ")\n"
                            + lblNaradi.getText()));
                }
            }
        }
    }

    private enum Akce {
        Cinnost, // řazení podle názvu činnosti
        Dolu,
        Nahoru,
        Predmet,
        MaterialPridatUbrat,
        NaradiPridatUbrat,
        Priorita,
        Vypsat;
    }

    private enum Order {
        OrdPriorita, OrdPredmet, OrdCinnost;
    }
}
