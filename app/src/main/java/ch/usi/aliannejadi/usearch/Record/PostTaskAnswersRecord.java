package ch.usi.aliannejadi.usearch.record;

/**
 * Created by jacopofidacaro on 21.09.17.
 */

public class PostTaskAnswersRecord implements Record {

    public boolean hurried;
    public boolean quickly;
    public boolean difficult;
    public boolean steps;
    public boolean surroundings;
    public boolean ignored;
    public boolean time;
    public boolean absorbed;
    public boolean enough;
    public boolean satisfied;

    public PostTaskAnswersRecord(boolean hurried,
                                 boolean quickly,
                                 boolean difficult,
                                 boolean steps,
                                 boolean surroundings,
                                 boolean ignored,
                                 boolean time,
                                 boolean absorbed,
                                 boolean enough,
                                 boolean satisfied) {
        this.hurried = hurried;
        this.quickly = quickly;
        this.difficult = difficult;
        this.steps = steps;
        this.surroundings = surroundings;
        this.ignored = ignored;
        this.time = time;
        this.absorbed = absorbed;
        this.enough = enough;
        this.satisfied = satisfied;

    }

    public String toString() {

        String res = "post task questionnaire answers record:\n";
        if (hurried) res += "  hurried\n";
        if (quickly) res += "  quickly\n";
        if (difficult) res += "  difficult\n";
        if (steps) res += "  steps\n";
        if (surroundings) res += "  surroundings\n";
        if (ignored) res += "  ignored\n";
        if (time) res += "  time\n";
        if (absorbed) res += "  absorbed\n";
        if (enough) res += "  enough\n";
        if (satisfied) res += "  satisfied\n";
        return res + ".";

    }





}
