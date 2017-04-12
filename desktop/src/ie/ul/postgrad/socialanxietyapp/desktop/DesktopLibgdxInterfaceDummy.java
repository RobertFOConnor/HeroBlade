package ie.ul.postgrad.socialanxietyapp.desktop;

import ie.ul.postgrad.socialanxietyapp.Avatar;
import ie.ul.postgrad.socialanxietyapp.LibGdxInterface;

/**
 * Created by Robert on 26/03/2017.
 * <p>
 * Dummy implementation of LibGdx interface for desktop testing.
 */

class DesktopLibgdxInterfaceDummy implements LibGdxInterface {


    @Override
    public void saveAvatar(Avatar avatar) {

    }

    @Override
    public Avatar getAvatar() {
        return new Avatar(2, 2);
    }

    @Override
    public int getNPCId() {
        return 0;
    }
}
