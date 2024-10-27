package practical;

public class ActorMovieCount {
    private final String actorName;
    private final int numMovies;

    public ActorMovieCount(String actorName, int numMovies) {
        this.actorName = actorName;
        this.numMovies = numMovies;
    }

    public String getActorName() {
        return actorName;
    }

    public int getNumMovies() {
        return numMovies;
    }
}
