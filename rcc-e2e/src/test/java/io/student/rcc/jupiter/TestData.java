package io.student.rcc.jupiter;

import io.student.rcc.model.api.ArtistJson;
import io.student.rcc.model.api.MuseumJson;
import io.student.rcc.model.api.PaintingJson;
import io.student.rcc.model.api.UserJson;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public record TestData(
        @Nullable UserJson user,
        @Nullable String userPassword,
        @Nullable ArtistJson artist,
        @Nullable MuseumJson museum,
        @Nullable PaintingJson painting
) {


    @Nonnull
    public TestData withUser(UserJson user, String password) {
        return new TestData(user, password, this.artist, this.museum, this.painting);
    }

    @Nonnull
    public TestData withArtist(ArtistJson artist) {
        return new TestData(this.user, this.userPassword, artist, this.museum, this.painting);
    }


    @Nonnull
    public TestData withMuseum(MuseumJson museum) {
        return new TestData(this.user, this.userPassword, this.artist, museum, this.painting);
    }

    @Nonnull
    public TestData withPainting(PaintingJson painting) {
        return new TestData(this.user, this.userPassword, this.artist, this.museum, painting);
    }
}
