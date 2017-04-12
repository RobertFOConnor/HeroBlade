package ie.ul.postgrad.socialanxietyapp;

/**
 * Created by Robert on 26/03/2017.
 *
 * Interface for allowing LibGdx to interact with parent activity in Android.
 */

public interface LibGdxInterface {

    void saveAvatar(Avatar avatar);

    Avatar getAvatar();

    int getNPCId();
}
