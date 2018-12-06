package demoaltp;

import demoaltp.database.HibernateUtil;
import demoaltp.modal.CauHoi;
import demoaltp.modal.MucDo;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 * FXML Controller class
 *
 * @author BaoBao
 */
public class ChoiGameController implements Initializable {

    private List<Text> lstMocDiem = null;
    private List<CauHoi> lstCauHoiDe = null;
    private List<CauHoi> lstCauHoiTB = null;
    private List<CauHoi> lstCauHoiKho = null;
    private List<MucDo> lstMucDo = null;
    private SessionFactory factory = null;
    private int viTriCauHoi = 0;
    private int mucDoCauHoi = 0;
    private CauHoi curCauHoi = null;
    private Alert msg;
    private int mocCauHoi = 0;
    @FXML
    private VBox vbCotPhai;
    @FXML
    private Label lbNoiDung;
    @FXML
    private Button btnChoiceA;
    @FXML
    private Button btnChoiceB;
    @FXML
    private Button btnChoiceC;
    @FXML
    private Button btnChoiceD;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        factory = HibernateUtil.getSessionFactory();
        msg = new Alert(Alert.AlertType.ERROR);
        msg.setTitle("Ai là triệu phú");

        this.initMocDiem();
        this.showMocDiem();

        this.getMucDo();
        this.newGame();
    }

    private void newGame() {
        this.viTriCauHoi = 0;
        this.mocCauHoi = 0;
        this.mucDoCauHoi = 0;
        if (lstCauHoiDe != null) {
            lstCauHoiDe.clear();
        }
        this.getCauHoiDe();
        if (lstCauHoiTB != null) {
            lstCauHoiTB.clear();
        }
        this.getCauHoiTB();
        if (lstCauHoiKho != null) {
            lstCauHoiKho.clear();
        }
        this.getCauHoiKho();
        this.showCauHoi();
        this.showMocHienTai();
    }

    @FXML
    private void chonDapAnHandler(ActionEvent event) {
        Button btnClicked = (Button) event.getSource();
        String answer = btnClicked.getText().substring(0, 1);

        if (answer.equals(curCauHoi.getDapAn())) {
            // Tra loi dung
            if (mocCauHoi < 14) {
                // Chua toi cau hoi so 15
                this.viTriCauHoi++;
                this.mocCauHoi++;
                this.showCauHoi();
                this.showMocHienTai();
                this.resetMocTruoc();
            } else {
                // Toi cau hoi so 15
                this.showDialogThongBao("Bạn đã chiến thắng", "Chúc mừng bạn đã xuất sắc vượt qua 15 câu hỏi.\nBạn có muốn chơi lại không?");
            }
        } else {
            // Tra loi sai
            this.showDialogThongBao("Bạn đã thua", "Bạn có muốn chơi lại không?");
        }
    }

    private void showDialogThongBao(String headerText, String contentText) {
        msg.setAlertType(Alert.AlertType.CONFIRMATION);
        msg.setHeaderText(headerText);
        msg.setContentText(contentText);

        Optional<ButtonType> option = msg.showAndWait();
        if (option.get() == ButtonType.OK) {
            this.mocCauHoi++;
            this.resetMocTruoc();
            this.newGame();
        } else {
            Platform.exit();
        }
    }

    private void resetMocTruoc() {
        if (mocCauHoi - 1 >= 0) {
            Text txtMocDiem = lstMocDiem.get(mocCauHoi - 1);
            txtMocDiem.setFill(Color.BLACK);
            txtMocDiem.setFont(Font.font(18));
        }
    }

    private void showMocHienTai() {
        if (mocCauHoi < 15) {
            Text txtMocDiem = lstMocDiem.get(mocCauHoi);
            txtMocDiem.setFill(Color.RED);
            txtMocDiem.setFont(Font.font(20));
        }
    }

    private void showCauHoi() {
        if (viTriCauHoi == 5) {
            mucDoCauHoi++;
            viTriCauHoi = 0;
        }

        switch (mucDoCauHoi) {
            case 0:
                curCauHoi = lstCauHoiDe.get(viTriCauHoi);
                break;
            case 1:
                curCauHoi = lstCauHoiTB.get(viTriCauHoi);
                break;
            case 2:
                curCauHoi = lstCauHoiKho.get(viTriCauHoi);
                break;
        }

        this.lbNoiDung.setText(curCauHoi.getNoiDung());
        this.btnChoiceA.setText(String.format("A. %s", curCauHoi.getDapAnA()));
        this.btnChoiceB.setText(String.format("B. %s", curCauHoi.getDapAnB()));
        this.btnChoiceC.setText(String.format("C. %s", curCauHoi.getDapAnC()));
        this.btnChoiceD.setText(String.format("D. %s", curCauHoi.getDapAnD()));
    }

    private void getMucDo() {
        Session session = factory.openSession();
        Criteria cr = session.createCriteria(MucDo.class);
        this.lstMucDo = cr.list();
        session.close();
    }

    private void getCauHoiDe() {
        Session session = factory.openSession();
        Criteria cr = session.createCriteria(CauHoi.class);
        cr.add(Restrictions.eq("mucDo", lstMucDo.get(0)));
        cr.add(Restrictions.sqlRestriction("1=1 ORDER BY RAND()"));
        cr.setMaxResults(5);
        this.lstCauHoiDe = cr.list();
        session.close();
    }

    private void getCauHoiTB() {
        Session session = factory.openSession();
        Criteria cr = session.createCriteria(CauHoi.class);
        cr.add(Restrictions.eq("mucDo", lstMucDo.get(1)));
        cr.add(Restrictions.sqlRestriction("1=1 ORDER BY RAND()"));
        cr.setMaxResults(5);
        this.lstCauHoiTB = cr.list();
        session.close();
    }

    private void getCauHoiKho() {
        Session session = factory.openSession();
        Criteria cr = session.createCriteria(CauHoi.class);
        cr.add(Restrictions.eq("mucDo", lstMucDo.get(2)));
        cr.add(Restrictions.sqlRestriction("1=1 ORDER BY RAND()"));
        cr.setMaxResults(5);
        this.lstCauHoiKho = cr.list();
        session.close();
    }
    
    private void showMocDiem() {
        for (int i = lstMocDiem.size() - 1; i >= 0; i--) {
            vbCotPhai.getChildren().add(lstMocDiem.get(i));
        }
    }

    private void initMocDiem() {
        if (lstMocDiem == null) {
            lstMocDiem = new ArrayList<>();
            lstMocDiem.add(new Text("1. 200"));
            lstMocDiem.add(new Text("2. 400"));
            lstMocDiem.add(new Text("3. 600"));
            lstMocDiem.add(new Text("4. 1,000"));
            lstMocDiem.add(new Text("5. 2,000"));
            lstMocDiem.add(new Text("6. 3,000"));
            lstMocDiem.add(new Text("7. 6,000"));
            lstMocDiem.add(new Text("8. 10,000"));
            lstMocDiem.add(new Text("9. 14,000"));
            lstMocDiem.add(new Text("10. 22,000"));
            lstMocDiem.add(new Text("11. 30,000"));
            lstMocDiem.add(new Text("12. 40,000"));
            lstMocDiem.add(new Text("13. 60,000"));
            lstMocDiem.add(new Text("14. 85,000"));
            lstMocDiem.add(new Text("15. 150,000"));
        }
    }
}