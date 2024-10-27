package practical;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Record;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.control.cell.PropertyValueFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class gui {
    Driver driver;
    Session session;
    @FXML
    private TableColumn<ActorMovieCount, String> actorName;

    @FXML
    private Label actorNum;

    @FXML
    private TableView<ActorMovieCount>actorPerMovie;

    @FXML
    private TableColumn<ActorMovieCount, Integer> numMovies;

    @FXML
    private Button queryButton;

    @FXML
    private ListView<String> sameMovie;

    @FXML
    void connectDB(MouseEvent event) {
        try{
            String uri = "bolt://localhost:7687";
            String user = "neo4j";
            String password = "qwerty123";
            String databaseName = "prac10neo4jb";  // Specify your database name here
            driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
            session = driver.session(org.neo4j.driver.SessionConfig.forDatabase(databaseName));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Connection Status");
            alert.setHeaderText(null);
            alert.setContentText("Connected to Neo4j database: " + databaseName);
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Status");
            alert.setHeaderText(null);
            alert.setContentText("Error connecting to Neo4j database");
            alert.showAndWait();
        }
        
    }

    @FXML
    void runQueries(MouseEvent event) {
        // Clear components
    actorNum.setText("0");
    actorPerMovie.getItems().clear();
    sameMovie.getItems().clear();

    int actorCount = countActors(session);
    actorNum.setText(String.valueOf(actorCount));
    Map<String, Integer> actorMovies = getMoviesPerActor(session);

    // Populate the TableView
    ObservableList<ActorMovieCount> actorMovieList = FXCollections.observableArrayList();
    for (Map.Entry<String, Integer> entry : actorMovies.entrySet()) {
        actorMovieList.add(new ActorMovieCount(entry.getKey(), entry.getValue()));
    }
    actorPerMovie.setItems(actorMovieList);

    List<String> actorNames = getActorsSameMovie(session);
    // Populate the ListView
    for (String actorName : actorNames) {
        sameMovie.getItems().add(actorName);
    }
    }


    public int countActors(Session session) {
        // Execute a Cypher query
        String query = "MATCH (a:Actor) RETURN count(a) AS actorCount";
        Result result = session.run(query);

        // Get the count of actors
        Record record = result.single();
        return record.get("actorCount").asInt();
    }

    public Map<String, Integer> getMoviesPerActor(Session session) {
        // Execute a Cypher query
        String query = "MATCH (a:Actor)-[:ACTED_IN]->(m:Movie) RETURN a.name AS actor, count(m) AS movieCount ORDER BY movieCount DESC LIMIT 100";
        Result result = session.run(query);
    
        // Initialize a map to store actor names and their movie counts
        Map<String, Integer> actorMovieCounts = new HashMap<>();
    
        // Iterate through the query results and populate the map
        while (result.hasNext()) {
            Record record = result.next();
            actorMovieCounts.put(record.get("actor").asString(), record.get("movieCount").asInt());
        }
    
        // Return the map
        return actorMovieCounts;
    }

    public List<String> getActorsSameMovie(Session session) {
        // Execute a Cypher query
        String query = "MATCH (a1:Actor)-[:ACTED_IN]->(m:Movie)<-[:ACTED_IN]-(a2:Actor) WHERE a1.name < a2.name RETURN a1.name AS actor1, a2.name AS actor2, m.title AS movie";
        Result result = session.run(query);
    
        // Initialize a set to store actor names (to avoid duplicates)
        Set<String> actorNamesSet = new HashSet<>();
    
        // Iterate through the query results and add actor names to the set
        while (result.hasNext()) {
            Record record = result.next();
            actorNamesSet.add(record.get("actor1").asString());
            actorNamesSet.add(record.get("actor2").asString());
        }
    
        // Convert the set to a list and return
        return new ArrayList<>(actorNamesSet);
    }

    @FXML
    public void initialize() {
        // Initialize the table columns
        actorName.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getActorName()));
        numMovies.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getNumMovies()).asObject());
    }
    
}


