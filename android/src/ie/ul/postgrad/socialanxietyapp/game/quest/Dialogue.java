package ie.ul.postgrad.socialanxietyapp.game.quest;

/**
 * Created by Robert on 07-Apr-17.
 */

public class Dialogue {

    private String speaker;
    private int speakerImage;
    private String[] sentences;
    private int lineIndex = 0;

    public Dialogue(String speaker, int speakerImage, String[] sentences) {
        this.speaker = speaker;
        this.speakerImage = speakerImage;
        this.sentences = sentences;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public int getSpeakerImage() {
        return speakerImage;
    }

    public void setSpeakerImage(int speakerImage) {
        this.speakerImage = speakerImage;
    }

    public String getSentence() {

        if(lineIndex < sentences.length) {
            String s = sentences[lineIndex];
            lineIndex++;
            return s;
        } else {
            return "";
        }
    }

    public void setSentences(String[] sentences) {
        this.sentences = sentences;
        lineIndex = 0;
    }
}
