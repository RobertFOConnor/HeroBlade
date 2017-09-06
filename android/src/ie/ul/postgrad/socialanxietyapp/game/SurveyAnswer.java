package ie.ul.postgrad.socialanxietyapp.game;

/**
 * Created by Robert on 04-Sep-17.
 */

public class SurveyAnswer {

    private int question;
    private int answer;
    private String date;

    public SurveyAnswer(int question, int answer, String date) {
        this.question = question;
        this.answer = answer;
        this.date = date;
    }

    public int getQuestion() {
        return question;
    }

    public int getAnswer() {
        return answer;
    }

    public String getDate() {
        return date;
    }
}
