package odb.gradle;

import java.sql.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import com.objectdb.o.STN.r;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class Controller {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory(
            "$objectdb/db/p2.odb");
    EntityManager em = emf.createEntityManager();

    @FXML
    private TextField accIDField;

    @FXML
    private TextField accNumField;

    @FXML
    private DatePicker transDate;

    @FXML
    private TableView<BankAccount> accTable;

    @FXML
    private TableView<Transaction> accTransactions;

    @FXML
    private TextField delAccField;

    @FXML
    private Button deleteButton;

    @FXML
    private Button getAccButton;

    @FXML
    private Button getTransButt;

    @FXML
    private TextField holderName;

    @FXML
    private ComboBox<Long> rAcc;

    @FXML
    private ComboBox<Long> sAcc;

    @FXML
    private Button saveAccButton;

    @FXML
    private Button saveButton;

    @FXML
    private TableColumn<BankAccount, Long> tableAccNum;

    @FXML
    private TableColumn<BankAccount, String> tableHolder;

    @FXML
    private TableColumn<Transaction, Double> transAmount;

    @FXML
    private TextField transAmountField;

    @FXML
    private TableColumn<Transaction, Date> transDateTBL;

    @FXML
    private TableColumn<Transaction, Long> transID;

    @FXML
    private TextField transIDField;

    @FXML
    private TableColumn<Transaction, String> transT;

    @FXML
    private ComboBox<String> transTypePick;

    @FXML
    void createAccount(MouseEvent event) {
        if (accIDField.getText().isEmpty() || holderName.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Missing Fields");
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
            return;
            
        }
        try {
            em.getTransaction().begin();
            BankAccount acc = new BankAccount();
            acc.setAccountNumber(Long.parseLong(accIDField.getText()));
            acc.setHolderName(holderName.getText());
            em.persist(acc);
            em.getTransaction().commit();
            try {
                TypedQuery<Long> query = em.createQuery("SELECT b.accountNumber FROM BankAccount b", Long.class);
                ObservableList<Long> accountNumbers = FXCollections.observableArrayList(query.getResultList());
                rAcc.setItems(accountNumbers);
                sAcc.setItems(accountNumbers);
            } catch (Exception e) {
                throw e;
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Account Created");
            alert.setContentText("Account created successfully.");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Account Creation Failed");
            alert.setContentText("An error occurred while creating the account.");
            alert.showAndWait();
            em.getTransaction().rollback();
        }
    }

    @FXML
    void enterChar(KeyEvent event) {
        String input = event.getText();
        KeyCode keyCode = event.getCode();
        if (keyCode == KeyCode.BACK_SPACE || keyCode == KeyCode.DELETE ||
                keyCode == KeyCode.LEFT || keyCode == KeyCode.RIGHT ||
                keyCode == KeyCode.HOME || keyCode == KeyCode.END) {
            holderName.setEditable(true);
            return;
        }
        if (!input.matches("[a-zA-Z]")) {
            // Prevents non-letter keys from being processed
            holderName.setEditable(false);
            return;
        }
        holderName.setEditable(true);

    }

    @FXML
    void enterNumber(KeyEvent event) {
        String character = event.getText();
        KeyCode keyCode = event.getCode();

        // Allow control keys like backspace, delete, arrow keys, etc.
        if (keyCode == KeyCode.BACK_SPACE || keyCode == KeyCode.DELETE ||
                keyCode == KeyCode.LEFT || keyCode == KeyCode.RIGHT ||
                keyCode == KeyCode.HOME || keyCode == KeyCode.END) {
            transIDField.setEditable(true);
            transAmountField.setEditable(true);
            accIDField.setEditable(true);
            delAccField.setEditable(true);
            accNumField.setEditable(true);
            return;
        }

        // Prevent non-digit and non-period keys from being processed
        if (!character.matches("[0-9.]")) {
            event.consume();
            transIDField.setEditable(false);
            transAmountField.setEditable(false);
            accIDField.setEditable(false);
            delAccField.setEditable(false);
            accNumField.setEditable(false);
        } else {
            transIDField.setEditable(true);
            transAmountField.setEditable(true);
            accIDField.setEditable(true);
            delAccField.setEditable(true);
            accNumField.setEditable(true);
        }
    }

    @FXML
    void getAllAccounts(MouseEvent event) {
        try {
            TypedQuery<BankAccount> query = em.createQuery("SELECT b FROM BankAccount b", BankAccount.class);

            // Clear existing items from the table
            accTable.getItems().clear();

            // Fetch results from the query and add them to an observable list
            ObservableList<BankAccount> accounts = FXCollections.observableArrayList(query.getResultList());

            // Add the accounts to the table
            accTable.setItems(accounts);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Accounts Retrieved");
            alert.setContentText("Accounts retrieved successfully.");
            alert.showAndWait();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Account Retrieval Failed");
            alert.setContentText("An error occurred while retrieving accounts.");
            alert.showAndWait();
        }

    }

    public void initialize() {
        transTypePick.getItems().addAll(FXCollections.observableArrayList("Deposit", "Withdrawal", "Transfer"));

        try {
            TypedQuery<Long> query = em.createQuery("SELECT b.accountNumber FROM BankAccount b", Long.class);
            if(query.getResultList().isEmpty()){
                return;
            }
            ObservableList<Long> accountNumbers = FXCollections.observableArrayList(query.getResultList());
            rAcc.setItems(accountNumbers);
            sAcc.setItems(accountNumbers);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tableAccNum.setCellValueFactory(new PropertyValueFactory<BankAccount, Long>("accountNumber"));
        tableHolder.setCellValueFactory(new PropertyValueFactory<BankAccount, String>("holderName"));

        transAmount.setCellValueFactory(new PropertyValueFactory<Transaction, Double>("amount"));
        transID.setCellValueFactory(new PropertyValueFactory<Transaction, Long>("id"));
        transDateTBL.setCellValueFactory(new PropertyValueFactory<Transaction, Date>("date"));
        transT.setCellValueFactory(new PropertyValueFactory<Transaction, String>("type"));
    }

    @FXML
    void getAllTransactions(MouseEvent event) {
        try {
            BankAccount account = em.find(BankAccount.class, Long.parseLong(accNumField.getText()));
            accTransactions.getItems().clear();
            if (account != null) {
                // Retrieve transactions where the account is either the sender or receiver
                TypedQuery<Transaction> query = em.createQuery(
                        "SELECT t FROM Transaction t WHERE t.sender = :account OR t.receiver = :account",
                        Transaction.class);
                query.setParameter("account", account);

                ObservableList<Transaction> transactions = FXCollections.observableArrayList(query.getResultList());
                accTransactions.setItems(transactions);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Transactions Retrieved");
                alert.setContentText("Transactions retrieved successfully.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Transaction Retrieval Failed");
            alert.setContentText("An error occurred while retrieving transactions.");
            alert.showAndWait();
        }
    }

    @FXML
    void deleteAccount(MouseEvent event) {
        if (delAccField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Account Number Required");
            alert.setContentText("Please enter an account number to delete.");
            alert.showAndWait();
            return;
        }
        try {
            em.getTransaction().begin();
            BankAccount account = em.find(BankAccount.class, Long.parseLong(delAccField.getText()));
            if (account != null) {
                // Retrieve transactions where the account is either the sender or receiver
                TypedQuery<Transaction> query = em.createQuery(
                        "SELECT t FROM Transaction t WHERE t.sender = :account OR t.receiver = :account",
                        Transaction.class);
                query.setParameter("account", account);
    
                List<Transaction> transactions = query.getResultList();
                for (Transaction transaction : transactions) {
                    em.remove(transaction); // Ensure transactions are managed before removing
                }
    
                em.remove(account); // Now remove the account
            }
            em.getTransaction().commit();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Account Deleted");
            alert.setContentText("Account deleted successfully.");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Account Deletion Failed");
            alert.setContentText("An error occurred while deleting the account.");
            alert.showAndWait();
            em.getTransaction().rollback();
        }
    }

    @FXML
    void saveTransaction(MouseEvent event) {
        if (transIDField.getText().isEmpty() || transAmountField.getText ().isEmpty() || transDate.getValue() == null || transTypePick.getValue() == null || sAcc.getValue() == null || rAcc.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Missing Fields");
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
            return;
        }
        try {
            em.getTransaction().begin();

            Transaction t = new Transaction();
            t.setId(Long.parseLong(transIDField.getText()));
            t.setAmount(Double.parseDouble(transAmountField.getText()));
            t.setDate(Date.valueOf(transDate.getValue()));
            t.setType(transTypePick.getValue());

            // Find the sender and receiver BankAccount entities by account number
            BankAccount sender = em.find(BankAccount.class, sAcc.getValue());
            BankAccount receiver = em.find(BankAccount.class, rAcc.getValue());
            System.out.println(sender.toString());
            System.out.println(receiver.toString());

            t.setSender(sender);
            t.setReceiver(receiver);

            sender.addReceivedTransaction(
                    new Transaction(t.getId(), t.getDate(), t.getAmount(), t.getType(), sender, receiver));
            receiver.addSentTransaction(
                    new Transaction(t.getId(), t.getDate(), t.getAmount(), t.getType(), sender, receiver));

            em.persist(t);

            em.merge(receiver);
            em.merge(sender);
            em.getTransaction().commit();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Transaction Saved");
            alert.setContentText("Transaction saved successfully.");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Transaction Failed");
            alert.setContentText("An error occurred while saving the transaction.");
            alert.showAndWait();

            em.getTransaction().rollback();
        }
    }

}
