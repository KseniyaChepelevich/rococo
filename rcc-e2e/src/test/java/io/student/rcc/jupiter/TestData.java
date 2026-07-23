package io.student.rcc.jupiter;

import io.student.rcc.model.api.ArtistJson;
import io.student.rcc.model.api.MuseumJson;
import io.student.rcc.model.api.PaintingJson;
import io.student.rcc.model.api.UserJson;

public record TestData(
        UserJson user,
        String userPassword,
        ArtistJson artist,
        MuseumJson museum,
        PaintingJson painting
) {

        public TestData withUser(UserJson user, String password) {
        return new TestData(user, password, this.artist, this.museum, this.painting);
    }

        public TestData withArtist(ArtistJson artist) {
        return new TestData(this.user, this.userPassword, artist, this.museum, this.painting);
    }

        public TestData withMuseum(MuseumJson museum) {
        return new TestData(this.user, this.userPassword, this.artist, museum, this.painting);
    }

        public TestData withPainting(PaintingJson painting) {
        return new TestData(this.user, this.userPassword, this.artist, this.museum, painting);
    }
}
