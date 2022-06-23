package me.neovitalism.pixelmonextension;

import com.pixelmonmod.pixelmon.api.pokedex.PokedexRegistrationStatus;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.species.Pokedex;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.api.util.helpers.SpeciesHelper;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class PixelmonExtension extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "pixelmon";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Neovitalism";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @NotNull
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        UUID playerUUID = player.getUniqueId();
        params = params.toLowerCase();
        if(params.length() > 5 && params.substring(0, 5).contains("party")) {
            try {
                int partySlot = Integer.parseInt(params.substring(5,6));
                if(partySlot < 1 || partySlot > 6) {
                    return "Invalid party slot.";
                }
                partySlot--;
                PlayerPartyStorage playerParty = StorageProxy.getParty(playerUUID);
                Pokemon pokemon = playerParty.get(partySlot);
//                Species species = PixelmonSpecies.fromNameOrDex(String nameOrDexNumber); Use for dex shit
//                playerParty.playerPokedex.countSeen(); Use for trainer dex shit
                if(pokemon == null) {
                    return "No Pokemon in that party slot.";
                }
                if(params.length() == 6) {
                    return pokemon.getLocalizedName();
                } else {
                    String extraParams = params.substring(6);
                    switch (extraParams) {
                        case "_nick":
                        case "_nickname":
                            String nickname = pokemon.getNickname();
                            if(nickname != null && !(Objects.equals(nickname, ""))) {
                                return pokemon.getNickname();
                            }
                            return pokemon.getLocalizedName();
                    }
                }
            } catch (NumberFormatException e) {
                return "Invalid placeholder.";
            }
        }
        return "Invalid placeholder.";
    }
}
