package ie.ul.postgrad.socialanxietyapp.desktop;

import ie.ul.postgrad.socialanxietyapp.Avatar;
import ie.ul.postgrad.socialanxietyapp.LibGdxInterface;

/**
 * Created by Robert on 26/03/2017.
 */

public class DesktopLibgdxInterfaceDummy implements LibGdxInterface {


    @Override
    public void saveAvatar(Avatar avatar) {

    }

    @Override
    public Avatar getAvatar() {
        return new Avatar(2, 2);
    }
}
