package me.neovitalism.pixelmonextension;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.pixelmonmod.pixelmon.api.economy.BankAccount;
import com.pixelmonmod.pixelmon.api.economy.BankAccountProxy;
import com.pixelmonmod.pixelmon.api.pokedex.PlayerPokedex;
import com.pixelmonmod.pixelmon.api.pokemon.Element;
import com.pixelmonmod.pixelmon.api.pokemon.Nature;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.ability.Ability;
import com.pixelmonmod.pixelmon.api.pokemon.species.Pokedex;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.pokemon.species.evs.EVYields;
import com.pixelmonmod.pixelmon.api.pokemon.species.gender.Gender;
import com.pixelmonmod.pixelmon.api.pokemon.species.stat.ImmutableBattleStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.api.pokemon.stats.evolution.Evolution;
import com.pixelmonmod.pixelmon.api.pokemon.stats.evolution.conditions.OreCondition;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.LakeTrioStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.MeltanStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.MewStats;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class PixelmonExtension extends PlaceholderExpansion {
    private static final List<String> HIDDEN_POWER_ELEMENTS = new ArrayList<>();
    private static final List<BattleStatsType> ALL_STATS = new ArrayList<>();
    private static final List<String> LAKE_GUARDIANS = new ArrayList<>();
    static {
        HIDDEN_POWER_ELEMENTS.add("Fighting");
        HIDDEN_POWER_ELEMENTS.add("Flying");
        HIDDEN_POWER_ELEMENTS.add("Poison");
        HIDDEN_POWER_ELEMENTS.add("Ground");
        HIDDEN_POWER_ELEMENTS.add("Rock");
        HIDDEN_POWER_ELEMENTS.add("Bug");
        HIDDEN_POWER_ELEMENTS.add("Ghost");
        HIDDEN_POWER_ELEMENTS.add("Steel");
        HIDDEN_POWER_ELEMENTS.add("Fire");
        HIDDEN_POWER_ELEMENTS.add("Water");
        HIDDEN_POWER_ELEMENTS.add("Grass");
        HIDDEN_POWER_ELEMENTS.add("Electric");
        HIDDEN_POWER_ELEMENTS.add("Psychic");
        HIDDEN_POWER_ELEMENTS.add("Ice");
        HIDDEN_POWER_ELEMENTS.add("Dragon");
        HIDDEN_POWER_ELEMENTS.add("Dark");
        ALL_STATS.add(BattleStatsType.HP);
        ALL_STATS.add(BattleStatsType.ATTACK);
        ALL_STATS.add(BattleStatsType.DEFENSE);
        ALL_STATS.add(BattleStatsType.SPECIAL_ATTACK);
        ALL_STATS.add(BattleStatsType.SPECIAL_DEFENSE);
        ALL_STATS.add(BattleStatsType.SPEED);
        LAKE_GUARDIANS.add("Azelf");
        LAKE_GUARDIANS.add("Mesprit");
        LAKE_GUARDIANS.add("Uxie");
    }

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
        return "1.0.6";
    }

    @NotNull
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if(params.endsWith("_")) return "Invalid placeholder.";
        UUID playerUUID = player.getUniqueId();
        PlayerPartyStorage playerParty = StorageProxy.getParty(playerUUID);
        params = params.toLowerCase();
        String parsed = "";
        boolean isParsed = false;
        String[] instructions = params.split("_");
        int length = instructions.length;
        if(!(instructions[0].equals(""))) {
            if(instructions[0].equals("dexsize")) {
                if(length == 1) return String.valueOf(Pokedex.pokedexSize); // %pixelmon_dexsize%
                if(length == 3 && instructions[1].equals("gen")) {
                    int generation = getGeneration(instructions[2]);
                    return (generation == -1) ? "Invalid generation." :
                            String.valueOf(PixelmonSpecies.getGenerationDex(generation).size()); // %pixelmon_dexsize_gen_[1-8]%
                }
            }
            if(length == 1 && instructions[0].equals("version")) return Pixelmon.getVersion(); // %pixelmon_version%
            if(length > 1 && instructions[0].equals("next") && instructions[1].equals("legend")) { // %pixelmon_next_legend…
                long nextSpawnTime = TimeUnit.MILLISECONDS.toSeconds(PixelmonSpawning.legendarySpawner.nextSpawnTime - System.currentTimeMillis());
                long hours;
                long minutes;
                long seconds;
                switch (length) {
                    case 2:
                        hours = TimeUnit.SECONDS.toHours(nextSpawnTime);
                        minutes = TimeUnit.SECONDS.toMinutes(nextSpawnTime - TimeUnit.HOURS.toSeconds(hours));
                        seconds = TimeUnit.SECONDS.toSeconds(nextSpawnTime - TimeUnit.MINUTES.toSeconds(minutes));
                        parsed = timeFormat(hours) + ":" + timeFormat(minutes) + ":" + timeFormat(seconds); // %pixelmon_next_legend%
                        break;
                    case 3:
                        if(instructions[2].equals("minutes")) { // %pixelmon_next_legend_minutes%
                            minutes = TimeUnit.SECONDS.toMinutes(nextSpawnTime);
                            seconds = TimeUnit.SECONDS.toSeconds(nextSpawnTime - TimeUnit.MINUTES.toSeconds(minutes));
                            parsed = timeFormat(minutes) + ":" + timeFormat(seconds);
                            break;
                        }
                        if(instructions[2].equals("seconds")) { // %pixelmon_next_legend_seconds%
                            parsed = String.valueOf(nextSpawnTime);
                            break;
                        }
                    case 4:
                        if(instructions[2].equals("minutes") && instructions[3].equals("rounded")) { // %pixelmon_next_legend_minutes_rounded%
                            parsed = String.valueOf(TimeUnit.SECONDS.toMinutes(nextSpawnTime));
                            break;
                        }
                }
                if(!(parsed.equals(""))) return parsed;
                // End Server Placeholders
            }
            if(length > 1 && instructions[0].equals("trainer")) { // %pixelmon_trainer…
                if(length >= 3 && instructions[1].equals("dex")) {
                    if(instructions[2].equals("caught")) {
                        if(length == 3) return String.valueOf(playerParty.playerPokedex.countCaught()); // %pixelmon_trainer_dex_caught%
                        if(length == 4) { // %pixelmon_trainer_dex_caught_[pokemonName,dexNumber]%
                            Species species = getSpecies(instructions[3]);
                            if(species == null) {
                                return instructions[3] + " is not a valid Pokemon.";
                            } else {
                                return String.valueOf(playerParty.playerPokedex.hasCaught(species));
                            }
                        }
                        if(length == 5 && instructions[3].equals("gen")) { // %pixelmon_trainer_dex_caught_gen_[1-8]%
                            int generation = getGeneration(instructions[4]);
                            return (generation == -1) ? "Invalid Generation." : String.valueOf(playerParty.playerPokedex.countCaught(generation));
                        }
                    }
                    if(instructions[2].equals("seen")) {
                        if(length == 3) return String.valueOf(playerParty.playerPokedex.countSeen()); // %pixelmon_trainer_dex_seen%
                        if(length == 4) {
                            Species species = getSpecies(instructions[3]);
                            if(species == null) {
                                return instructions[3] + " is not a valid Pokemon.";
                            } else {
                                return String.valueOf(playerParty.playerPokedex.hasSeen(species));
                            }
                        }
                        if(length == 5 && instructions[3].equals("gen")) { // %pixelmon_trainer_dex_seen_gen_[1-8]%
                            int generation = getGeneration(instructions[4]);
                            return (generation == -1) ? "Invalid Generation." : String.valueOf(playerParty.playerPokedex.countSeen(generation));
                        }
                    }
                    if(instructions.length >= 4 && instructions[2].equals("percent")) { // %pixelmon_trainer_dex_percent…
                        if(instructions[3].equals("caught")) { // %pixelmon_trainer_dex_percent_caught…
                            if(length == 4) return dexPercent(playerParty.playerPokedex, 0, false, false, true); // %pixelmon_trainer_dex_percent_caught%
                            if(length == 5 && instructions[4].equals("rounded")) return
                                    dexPercent(playerParty.playerPokedex, 0, true, false, true); // %pixelmon_trainer_dex_percent_caught_rounded%
                            if(length == 5 && instructions[4].equals("int")) return
                                    dexPercent(playerParty.playerPokedex, 0, false, true, true); // %pixelmon_trainer_dex_percent_caught_int%
                            if(length >= 6 && instructions[4].equals("gen")) {
                                int generation = getGeneration(instructions[5]);
                                if(length == 6) return (generation == -1) ? "Invalid Generation." :
                                        dexPercent(playerParty.playerPokedex, generation, false, false, true); // %pixelmon_trainer_dex_percent_caught_gen_[1-8]%
                                if(length == 7 && instructions[6].equals("rounded")) return (generation == -1) ? "Invalid Generation." :
                                        dexPercent(playerParty.playerPokedex, generation, true, false, true); // %pixelmon_trainer_dex_percent_caught_gen_[1-8]_rounded%
                                if(length == 7 && instructions[6].equals("int")) return (generation == -1) ? "Invalid Generation." :
                                        dexPercent(playerParty.playerPokedex, generation, false, true, true); // %pixelmon_trainer_dex_percent_caught_gen_[1-8]_int%
                            }
                        }
                        if(instructions[3].equals("seen")) { // %pixelmon_trainer_dex_percent_seen…
                            if(length == 4) return dexPercent(playerParty.playerPokedex, 0, false, false, false); // %pixelmon_trainer_dex_percent_seen%
                            if(length == 5 && instructions[4].equals("rounded")) return
                                    dexPercent(playerParty.playerPokedex, 0, true, false, false); // %pixelmon_trainer_dex_percent_seen_rounded%
                            if(length == 5 && instructions[4].equals("int")) return
                                    dexPercent(playerParty.playerPokedex, 0, false, true, false); // %pixelmon_trainer_dex_percent_seen_int%
                            if(length >= 6 && instructions[4].equals("gen")) {
                                int generation = getGeneration(instructions[5]);
                                if(length == 6) return (generation == -1) ? "Invalid Generation." :
                                        dexPercent(playerParty.playerPokedex, generation, false, false, false); // %pixelmon_trainer_dex_percent_seen_gen_[1-8]%
                                if(length == 7 && instructions[6].equals("rounded")) return (generation == -1) ? "Invalid Generation." :
                                        dexPercent(playerParty.playerPokedex, generation, true, false, false); // %pixelmon_trainer_dex_percent_seen_gen_[1-8]_rounded%
                                if(length == 7 && instructions[6].equals("int")) return (generation == -1) ? "Invalid Generation." :
                                        dexPercent(playerParty.playerPokedex, generation, false, true, false); // %pixelmon_trainer_dex_percent_seen_gen_[1-8]_int%
                            }
                        }
                    }
                    if(instructions[2].equals("completed")) {
                        if(length == 3) return String.valueOf(playerParty.playerPokedex.countCaught() == Pokedex.pokedexSize); // %pixelmon_trainer_dex_completed%
                        if(length == 5 && instructions[3].equals("gen")) {
                            int generation = getGeneration(instructions[4]);
                            return (generation == -1) ? "Invalid Generation." : String.valueOf(playerParty.playerPokedex.countCaught(generation)
                                    == PixelmonSpecies.getGenerationDex(generation).size()); // %pixelmon_trainer_dex_completed_gen_[1-8]%
                        }
                    }
                }
                if(length == 2 && instructions[1].equals("balance")) { // %pixelmon_trainer_balance%
                   Optional<? extends BankAccount> playerAccount = BankAccountProxy.getBankAccount(playerUUID);
                   if(playerAccount.isEmpty()) return "0";
                   return String.format("%.2f", playerAccount.get().getBalance());
                }
                if(length == 3 && instructions[1].equals("highest") && instructions[2].equals("level"))
                    return String.valueOf(playerParty.getHighestLevel()); // %pixelmon_trainer_highest_level%
                if(length == 3 && instructions[1].equals("lowest") && instructions[2].equals("level"))
                    return String.valueOf(playerParty.getLowestLevel()); // %pixelmon_trainer_lowest_level%
                if(length == 3 && instructions[1].equals("average") && instructions[2].equals("level"))
                    return String.valueOf(playerParty.getAverageLevel()); // %pixelmon_trainer_average_level%
                if(length == 2 && instructions[1].equals("wins")) return (playerParty.stats == null) ?
                        String.valueOf(0) : String.valueOf(playerParty.stats.getWins()); // %pixelmon_trainer_wins%
                if(length == 2 && instructions[1].equals("losses")) return (playerParty.stats == null) ?
                        String.valueOf(0) : String.valueOf(playerParty.stats.getLosses()); // %pixelmon_trainer_losses%
                if(length >= 3 && instructions[1].equals("wl") && instructions[2].equals("ratio")) { // %pixelmon_trainer_wl_ratio…
                    double wlRatio = (playerParty.stats == null) ? 0 : (playerParty.stats.getLosses() == 0) ? playerParty.stats.getWins() :
                            (double) playerParty.stats.getWins() / playerParty.stats.getLosses();
                    if(length == 3) return String.valueOf(wlRatio); // %pixelmon_trainer_wl_ratio%
                    if(length == 4 && instructions[3].equals("rounded")) return String.format("%.2f", wlRatio); // %pixelmon_trainer_wl_ratio_rounded%
                }
                // End Trainer Placeholders
            }
            boolean partyExtension = false;
            Pokemon pokemon = null;
            if(length >= 3) {
                if (instructions[0].equals("party")) { // %pixelmon_party_[1-6]…
                    partyExtension = true;
                    int partySlot;
                    try {
                        partySlot = Integer.parseInt(instructions[1]);
                        if (partySlot < 1 || partySlot > 6) return partySlot + "";
                    } catch (NumberFormatException e) {
                        return instructions[1] + " is not a number.";
                    }
                    pokemon = playerParty.get(partySlot - 1);
                    if (pokemon == null) return "";
                    switch (instructions[2]) {
                        case "nickname":
                            String nickname = pokemon.getNickname();
                            if (length == 3) { // %pixelmon_party_[1-6]_nickname%
                                if (nickname == null || nickname.isEmpty()) {
                                    parsed = pokemon.getLocalizedName();
                                } else {
                                    parsed = nickname;
                                }
                            }
                            if (length == 4 && instructions[3].equals("formatted")) { // %pixelmon_party_[1-6]_nickname_formatted%
                                if (nickname == null || nickname.isEmpty()) {
                                    String form = pokemon.getForm().getLocalizedName();
                                    if (form.equals("None")) {
                                        parsed = pokemon.getLocalizedName();
                                    } else {
                                        parsed = form + " " + pokemon.getLocalizedName();
                                    }
                                } else {
                                    parsed = nickname;
                                }
                            }
                            break;
                        case "level": // %pixelmon_party_[1-6]_level%
                            if (length == 3) parsed = String.valueOf(pokemon.getPokemonLevel());
                            break;
                        case "exp": // %pixelmon_party_[1-6]_exp%
                            if (length == 3) parsed = String.valueOf(pokemon.getExperience());
                            if (length == 5 && instructions[3].equals("to") && instructions[4].equals("level"))
                                parsed = String.valueOf(pokemon.getExperienceToLevelUp());
                            break;
                        case "hp": // %pixelmon_party_[1-6]_hp%
                            if (length == 3) parsed = String.valueOf(pokemon.getHealth());
                            break;
                        case "maxhp": // %pixelmon_party_[1-6]_maxhp%
                            if (length == 3) parsed = String.valueOf(pokemon.getMaxHealth());
                            break;
                        case "atk": // %pixelmon_party_[1-6]_atk%
                            if (length == 3) parsed = String.valueOf(pokemon.getStat(BattleStatsType.ATTACK));
                            break;
                        case "def": // %pixelmon_party_[1-6]_def%
                            if (length == 3) parsed = String.valueOf(pokemon.getStat(BattleStatsType.DEFENSE));
                            break;
                        case "spatk": // %pixelmon_party_[1-6]_spatk%
                            if (length == 3) parsed = String.valueOf(pokemon.getStat(BattleStatsType.SPECIAL_ATTACK));
                            break;
                        case "spdef": // %pixelmon_party_[1-6]_spatk%
                            if (length == 3) parsed = String.valueOf(pokemon.getStat(BattleStatsType.SPECIAL_DEFENSE));
                            break;
                        case "speed": // %pixelmon_party_[1-6]_speed%
                            if (length == 3) parsed = String.valueOf(pokemon.getStat(BattleStatsType.SPEED));
                            break;
                        case "iv": // %pixelmon_party_[1-6]_iv…
                            if (length >= 4) {
                                switch (instructions[3]) {
                                    case "hp": // %pixelmon_party_[1-6]_iv_hp%
                                        if (length == 4)
                                            parsed = String.valueOf(pokemon.getIVs().getStat(BattleStatsType.HP));
                                        break;
                                    case "atk": // %pixelmon_party_[1-6]_iv_atk%
                                        if (length == 4)
                                            parsed = String.valueOf(pokemon.getIVs().getStat(BattleStatsType.ATTACK));
                                        break;
                                    case "def": // %pixelmon_party_[1-6]_iv_def%
                                        if (length == 4)
                                            parsed = String.valueOf(pokemon.getIVs().getStat(BattleStatsType.DEFENSE));
                                        break;
                                    case "spatk": // %pixelmon_party_[1-6]_iv_spatk%
                                        if (length == 4)
                                            parsed = String.valueOf(pokemon.getIVs().getStat(BattleStatsType.SPECIAL_ATTACK));
                                        break;
                                    case "spdef": // %pixelmon_party_[1-6]_iv_spdef%
                                        if (length == 4)
                                            parsed = String.valueOf(pokemon.getIVs().getStat(BattleStatsType.SPECIAL_DEFENSE));
                                        break;
                                    case "speed": // %pixelmon_party_[1-6]_iv_speed%
                                        if (length == 4)
                                            parsed = String.valueOf(pokemon.getIVs().getStat(BattleStatsType.SPEED));
                                        break;
                                    case "total": // %pixelmon_party_[1-6]_iv_total…
                                        double total = 0;
                                        for (BattleStatsType stat : ALL_STATS) {
                                            total += pokemon.getIVs().getStat(stat);
                                        }
                                        if (length == 4)
                                            parsed = String.valueOf((int) total); // %pixelmon_party_[1-6]_iv_total%
                                        if (length >= 5 && instructions[4].equals("percent")) {
                                            if (length == 5)
                                                parsed = String.format("%.2f", (total / 186) * 100); // %pixelmon_party_[1-6]_iv_total_percent%
                                            if (length == 6 && instructions[5].equals("int"))
                                                parsed = String.valueOf((int) (Math.floor(total / 186 * 100))); // %pixelmon_party_[1-6]_iv_total_percent_int%
                                        }
                                        break;
                                }
                            }
                            break;
                        case "ev": // %pixelmon_party_[1-6]_ev…
                            if (length >= 4) {
                                switch (instructions[3]) {
                                    case "hp": // %pixelmon_party_[1-6]_ev_hp%
                                        if (length == 4)
                                            parsed = String.valueOf(pokemon.getEVs().getStat(BattleStatsType.HP));
                                        break;
                                    case "atk": // %pixelmon_party_[1-6]_ev_atk%
                                        if (length == 4)
                                            parsed = String.valueOf(pokemon.getEVs().getStat(BattleStatsType.ATTACK));
                                        break;
                                    case "def": // %pixelmon_party_[1-6]_ev_def%
                                        if (length == 4)
                                            parsed = String.valueOf(pokemon.getEVs().getStat(BattleStatsType.DEFENSE));
                                        break;
                                    case "spatk": // %pixelmon_party_[1-6]_ev_spatk%
                                        if (length == 4)
                                            parsed = String.valueOf(pokemon.getEVs().getStat(BattleStatsType.SPECIAL_ATTACK));
                                        break;
                                    case "spdef": // %pixelmon_party_[1-6]_ev_spdef%
                                        if (length == 4)
                                            parsed = String.valueOf(pokemon.getEVs().getStat(BattleStatsType.SPECIAL_DEFENSE));
                                        break;
                                    case "speed": // %pixelmon_party_[1-6]_ev_speed%
                                        if (length == 4)
                                            parsed = String.valueOf(pokemon.getEVs().getStat(BattleStatsType.SPEED));
                                        break;
                                    case "total": // %pixelmon_party_[1-6]_ev_total…
                                        double total = 0;
                                        for (BattleStatsType stat : ALL_STATS) {
                                            total += pokemon.getEVs().getStat(stat);
                                        }
                                        if (length == 4)
                                            parsed = String.valueOf((int) total); // %pixelmon_party_[1-6]_ev_total%
                                        if (length >= 5 && instructions[4].equals("percent")) {
                                            if (length == 5)
                                                parsed = String.format("%.2f", (total / 510) * 100); // %pixelmon_party_[1-6]_ev_total_percent%
                                            if (length == 6 && instructions[5].equals("int"))
                                                parsed = String.valueOf((int) (Math.floor(total / 510 * 100))); // %pixelmon_party_[1-6]_ev_total_percent_int%
                                        }
                                        break;
                                }
                            }
                            break;
                        case "move": // %pixelmon_party_[1-6]_move…
                            if (length >= 4) {
                                int moveSlot;
                                try {
                                    moveSlot = Integer.parseInt(instructions[3]);
                                    if (moveSlot < 1 || moveSlot > 4) return moveSlot + " is not a valid move number.";
                                } catch (NumberFormatException e) {
                                    return instructions[3] + " is not a number.";
                                }
                                String move = pokemon.getMoveset().get(moveSlot - 1).toString();
                                if (length == 4)
                                    parsed = (move == null) ? "None" : move; // %pixelmon_party_[1-6]_move_[1-4]%
                                if (length == 5 && instructions[4].equals("unlocalized"))
                                    parsed = (move == null) ? "None" : unlocalize(move); // %pixelmon_party_[1-6]_move_[1-4]_unlocalized%
                            }
                            break;
                        case "moves": // %pixelmon_party_[1-6]_moves…
                            String moveset = Arrays.toString(pokemon.getMoveset().attacks);
                            moveset = moveset.substring(1, moveset.length()-1);
                            String[] moves = moveset.split(", ");
                            if (length == 3) parsed = listFunction(moves, ", ", true); // %pixelmon_party_[1-6]_moves%
                            if (length >= 4 && instructions[3].contains("s:"))
                                parsed = listFunction(moves, instructionsLeft(3, instructions), true); // %pixelmon_party_[1-6]_moves_s:[separator]%
                            if (length == 4 && instructions[3].equals("unlocalized"))
                                parsed = listFunction(moves, ", ", false); // %pixelmon_party_[1-6]_moves_unlocalized%
                            if (length >= 5 && instructions[3].equals("unlocalized") && instructions[4].contains("s:"))
                                parsed = listFunction(moves, instructionsLeft(4, instructions), false); // %pixelmon_party_[1-6]_moves_unlocalized_s:[separator]%
                            break;
                        case "friendship": // %pixelmon_party_[1-6]_friendship%
                            if (length == 3) parsed = String.valueOf(pokemon.getFriendship());
                            break;
                        case "ability": // %pixelmon_party_[1-6]_ability…
                            if (length == 3)
                                parsed = pokemon.getAbility().getLocalizedName(); // %pixelmon_party_[1-6]_ability%
                            if (length == 4 && instructions[3].equals("unlocalized"))
                                parsed = unlocalize(pokemon.getAbility().getLocalizedName()); // %pixelmon_party_[1-6]_unlocalized%
                            break;
                        case "gender": // %pixelmon_party_[1-6]_gender%
                            if (length == 3) parsed = pokemon.getGender().getLocalizedName();
                            if (length == 4 && instructions[3].equals("int")) { // %pixelmon_party_[1-6]_gender_int%
                                Gender gender = pokemon.getGender();
                                switch(gender.toString()) {
                                    case "MALE":
                                        parsed = "0";
                                        break;
                                    case "FEMALE":
                                        parsed = "1";
                                        break;
                                    default:
                                        parsed = "2";
                                        break;
                                }
                            }
                            break;
                        case "ball": // %pixelmon_party_[1-6]_ball%
                            if (length == 3) parsed = pokemon.getBall().getLocalizedName();
                            if (length == 4 && instructions[3].equals("unlocalized"))
                                parsed = unlocalize(pokemon.getBall().getLocalizedName());
                            break;
                        case "nature": // %pixelmon_party_[1-6]_nature…
                            if (length == 3)
                                parsed = pokemon.getNature().getLocalizedName(); // %pixelmon_party_[1-6]_nature%
                            if (length == 4) {
                                if (instructions[3].equals("base"))
                                    parsed = pokemon.getBaseNature().getLocalizedName(); // %pixelmon_party_[1-6]_nature_base%
                                if (instructions[3].equals("mint")) { // %pixelmon_party_[1-6]_nature_mint%
                                    Nature mintNature = pokemon.getMintNature();
                                    parsed = (mintNature == null) ? "None" : mintNature.getLocalizedName();
                                }
                                if (instructions[3].equals("minted")) { // %pixelmon_party_[1-6]_nature_minted%
                                    Nature mintNature = pokemon.getMintNature();
                                    parsed = String.valueOf(mintNature == null);
                                }
                            }
                            break;
                        case "form": // %pixelmon_party_[1-6]_form…
                            if (length == 3)
                                parsed = pokemon.getForm().getLocalizedName(); // %pixelmon_party_[1-6]_form%
                            if (length == 4 && instructions[3].equals("unlocalized")) {
                                isParsed = true;
                                parsed = pokemon.getForm().getName();
                            } // %pixelmon_party_[1-6]_form_unlocalized%
                            break;
                        case "palette": // %pixelmon_party_[1-6]_palette…
                            if (length == 3)
                                parsed = pokemon.getPalette().getLocalizedName(); // %pixelmon_party_[1-6]_palette%
                            if (length == 4 && instructions[3].equals("unlocalized"))
                                parsed = pokemon.getPalette().getName(); // %pixelmon_party_[1-6]_palette_unlocalized%
                            break;
                        case "growth": // %pixelmon_party_[1-6]_growth%
                            if (length == 3) parsed = pokemon.getGrowth().getLocalizedName();
                            break;
                        case "hiddenpower": // %pixelmon_party_[1-6]_hiddenpower%
                            if (length == 3) parsed = getHiddenPower(pokemon);
                            break;
                        case "shiny": // %pixelmon_party_[1-6]_shiny%
                            if (length == 3) parsed = String.valueOf(pokemon.getPalette().getName().contains("shiny"));
                            break;
                        case "unbreedable": // %pixelmon_party_[1-6]_unbreedable%
                            if (length == 3) parsed = String.valueOf(pokemon.isUnbreedable());
                            break;
                        case "untradeable": // %pixelmon_party_[1-6]_untradeable%
                            if (length == 3) parsed = String.valueOf(pokemon.isUntradeable());
                            break;
                        case "ot": // %pixelmon_party_[1-6]_ot…
                            String ot = pokemon.getOriginalTrainer();
                            if (length == 3) parsed = (ot == null) ? "null" : ot; // %pixelmon_party_[1-6]_ot%
                            if (length == 4) {
                                UUID otUUID = pokemon.getOriginalTrainerUUID();
                                if (otUUID != null) {
                                    if (instructions[3].equals("uuid"))
                                        parsed = String.valueOf(otUUID); // %pixelmon_party_[1-6]_ot_uuid%
                                    if (instructions[3].equals("check"))
                                        parsed = String.valueOf(otUUID.equals(playerUUID)); // %pixelmon_party_[1-6]_ot_check%
                                }
                            }
                            break;
                        case "egg": // %pixelmon_party_[1-6]_egg…
                            boolean isEgg = pokemon.isEgg();
                            if (length == 3) parsed = String.valueOf(isEgg); // %pixelmon_party_[1-6]_egg%
                            if (length == 5 && instructions[3].equals("steps")) { // %pixelmon_party_[1-6]_egg_steps…
                                int stepsPerEggCycle = PixelmonConfigProxy.getBreeding().getStepsPerEggCycle();
                                int maxCycle = (pokemon.isDefaultForm()) ? pokemon.getSpecies().getDefaultForm().getEggCycles() :
                                        pokemon.getSpecies().getForm(pokemon.getForm().getName()).getEggCycles();
                                int stepsTaken = (maxCycle - pokemon.getEggCycles()) * stepsPerEggCycle + pokemon.getEggSteps();
                                if (instructions[4].equals("taken"))
                                    parsed = (pokemon.isEgg()) ? String.valueOf(stepsTaken) :
                                            "This pokemon is not an egg."; // %pixelmon_party_[1-6]_egg_steps_taken%
                                if (instructions[4].equals("left"))
                                    parsed = (pokemon.isEgg()) ? String.valueOf(((maxCycle * stepsPerEggCycle)
                                            + stepsPerEggCycle) - stepsTaken) : "This pokemon is not an egg."; // %pixelmon_party_[1-6]_egg_steps_left%
                            }
                            break;
                        case "mew":
                            if (length >= 4 && instructions[3].equals("clones")) {
                                if (pokemon.getSpecies().getName().equals("Mew")) {
                                    MewStats mewStats = (MewStats) pokemon.getExtraStats();
                                    if (length == 4)
                                        parsed = String.valueOf(mewStats.numCloned); // %pixelmon_party_[1-6]_mew_clones%
                                    if (length == 5 && instructions[4].equals("left"))
                                        parsed = String.valueOf(MewStats.MAX_CLONES -
                                                mewStats.numCloned); // %pixelmon_party_[1-6]_mew_clones_left%
                                } else {
                                    parsed = "Invalid Pokemon.";
                                }
                                break;
                            }
                        case "laketrio":
                            if (length >= 4 && instructions[3].equals("rubies")) {
                                if (LAKE_GUARDIANS.contains(pokemon.getSpecies().getName())) {
                                    LakeTrioStats lakeTrioStats = (LakeTrioStats) pokemon.getExtraStats();
                                    if (length == 4)
                                        parsed = String.valueOf(lakeTrioStats.numEnchanted); // %pixelmon_party_[1-6]_laketrio_rubies%
                                    if (length == 5 && instructions[4].equals("left"))
                                        parsed = String.valueOf(PixelmonConfigProxy.getGeneral().getLakeTrioMaxEnchants()
                                                - lakeTrioStats.numEnchanted); // %pixelmon_party_[1-6]_laketrio_rubies_left%
                                } else {
                                    parsed = "Invalid Pokemon.";
                                }
                            }
                            break;
                        case "meltan":
                            if (length >= 4 && instructions[3].equals("ores")) {
                                if (pokemon.getSpecies().getName().equals("Meltan")) {
                                    MeltanStats meltanStats = (MeltanStats) pokemon.getExtraStats();
                                    if (length == 4)
                                        parsed = String.valueOf(meltanStats.oresSmelted); // %pixelmon_party_[1-6]_meltan_ores%
                                    if (length == 5 && instructions[4].equals("left")) {
                                        List<Evolution> evolutionData = pokemon.getSpecies().getDefaultForm().getEvolutions();
                                        List<OreCondition> oreCondition = evolutionData.get(0).getConditionsOfType(OreCondition.class);
                                        parsed = String.valueOf(oreCondition.get(0).ores - meltanStats.oresSmelted); // %pixelmon_party_[1-6]_meltan_ores_left%
                                    }
                                } else {
                                    parsed = "Invalid Pokemon.";
                                }
                            }
                            break;
                        case "held":
                            if (length == 4 && instructions[3].equals("item")) { // %pixelmon_party_[1-6]_held_item%
                                String heldItem = pokemon.getHeldItem().getDisplayName().getString();
                                parsed = (heldItem.equals("[Air]")) ? "None" : heldItem.substring(1, heldItem.length() - 1);
                            }
                            break;
                        // End Party Placeholders
                    }
                    if(!(parsed.equals("")) || isParsed) return parsed;
                }
                if (instructions[0].equals("pokedex") || partyExtension) {
                    Species species;
                    String formName;
                    if (partyExtension) {
                        species = pokemon.getSpecies();
                        formName = (pokemon.isDefaultForm()) ? "None" : pokemon.getForm().getName();
                    } else {
                        species = getSpecies(instructions[1]);
                        if (species == null) return "Invalid Pokemon.";
                        formName = getFormName(instructions[1]);
                        if(formName == null) return "Invalid Form.";
                        if(formName.equals("")) return "Invalid Placeholder.";
                    }
                    Stats stats;
                    if(formName.equals("None")) {
                        stats = species.getDefaultForm();
                    } else {
                        stats = species.getForm(formName);
                    }
                    switch (instructions[2]) {
                        case "dex":
                            if(length == 4 && instructions[3].equals("number")) parsed = String.valueOf(species.getDex()); // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_dex_number%
                            break;
                        case "gen": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_gen%
                            if(length == 3) parsed = String.valueOf(species.getGeneration());
                            break;
                        case "name":
                            if(length == 3) parsed = species.getLocalizedName(); // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_name%
                            if(length == 4 && instructions[3].equals("formatted")) { // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_name_formatted%
                                if (formName.equals("None")) {
                                    parsed = species.getLocalizedName();
                                } else {
                                    parsed = formName + " " + species.getLocalizedName();
                                }
                            }
                            break;
                        case "basestat": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_basestat…
                            if(length == 4) {
                                ImmutableBattleStats battleStats = stats.getBattleStats();
                                switch (instructions[3]) {
                                    case "hp": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_basestat_hp%
                                        parsed = String.valueOf(battleStats.getStat(BattleStatsType.HP));
                                        break;
                                    case "atk": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_basestat_atk%
                                        parsed = String.valueOf(battleStats.getStat(BattleStatsType.ATTACK));
                                        break;
                                    case "def": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_basestat_def%
                                        parsed = String.valueOf(battleStats.getStat(BattleStatsType.DEFENSE));
                                        break;
                                    case "spatk": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_basestat_spatk%
                                        parsed = String.valueOf(battleStats.getStat(BattleStatsType.SPECIAL_ATTACK));
                                        break;
                                    case "spdef": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_basestat_spdef%
                                        parsed = String.valueOf(battleStats.getStat(BattleStatsType.SPECIAL_DEFENSE));
                                        break;
                                    case "speed": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_basestat_speed%
                                        parsed = String.valueOf(battleStats.getStat(BattleStatsType.SPEED));
                                        break;
                                    case "total": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_basestat_total%
                                        int baseStatTotal = 0;
                                        for(BattleStatsType b : ALL_STATS) {
                                            baseStatTotal+=battleStats.getStat(b);
                                        }
                                        parsed = String.valueOf(baseStatTotal);
                                        break;
                                }
                            }
                            break;
                        case "abilities":
                            StringBuilder sb = new StringBuilder();
                            for(Ability ability : stats.getAbilities().getAll()) {
                                sb.append(ability.getLocalizedName()).append(", ");
                            }
                            String[] ab = sb.substring(0, sb.toString().length()-2).split(", ");
                            if (length == 3) parsed = listFunction(ab, ", ", true);  // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_abilities%
                            if (length >= 4 && instructions[3].contains("s:"))
                                parsed = listFunction(ab, instructionsLeft(3, instructions), true);  // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_abilities_s:[separator]%
                            if (length == 4 && instructions[3].equals("unlocalized"))
                                parsed = listFunction(ab, ", ", false); // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_abilities_unlocalized%
                            if (length >= 5 && instructions[3].equals("unlocalized") && instructions[4].contains("s:"))
                                parsed = listFunction(ab, instructionsLeft(4, instructions), false); // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_abilities_unlocalized_s:[separator]%
                            break;
                        case "ability":
                            if(length >= 4) {
                                switch(instructions[3]) {
                                    case "1":
                                        if(length == 4) parsed = stats.getAbilities().getAbilities()[0].getLocalizedName();
                                        // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_ability_1%
                                        if(length == 5 && instructions[4].equals("unlocalized")) parsed = unlocalize(stats.getAbilities().getAbilities()[0].getLocalizedName());
                                        // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_ability_1_unlocalized%
                                        break;
                                    case "2":
                                        boolean hasSecond = (stats.getAbilities().getAbilities().length == 2);
                                        if(length == 4) parsed = (hasSecond) ? stats.getAbilities().getAbilities()[1].getLocalizedName()
                                                : "None"; // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_ability_2%
                                        if(length == 5 && instructions[4].equals("unlocalized")) parsed = (hasSecond) ?
                                                unlocalize(stats.getAbilities().getAbilities()[1].getLocalizedName())
                                                : "None"; // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_ability_2_unlocalized%
                                        break;
                                    case "ha":
                                        if(length == 4) parsed = (stats.getAbilities().hasHiddenAbilities()) ?
                                                stats.getAbilities().getHiddenAbilities()[0].getLocalizedName() : "None";
                                        // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_ability_ha%
                                        if(length == 5 && instructions[4].equals("unlocalized")) parsed = (stats.getAbilities().hasHiddenAbilities()) ?
                                                unlocalize(stats.getAbilities().getHiddenAbilities()[0].getLocalizedName())
                                                : "None"; // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_ability_ha_unlocalized%
                                        break;
                                }
                            }
                        case "islegend": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_isLegend%
                            if(length == 3) parsed = String.valueOf(species.isLegendary() || species.isMythical());
                            break;
                        case "ismythical": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_isMythical%
                            if(length == 3) parsed = String.valueOf(species.isMythical());
                            break;
                        case "isub": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_isUB%
                            if(length == 3) parsed = String.valueOf(species.isUltraBeast());
                            break;
                        case "islegendorub": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_isLegendOrUB%
                            if(length == 3) parsed = String.valueOf(species.isLegendary() || species.isUltraBeast() || species.isMythical());
                            break;
                        case "egg":
                            if(length >= 4 && instructions[3].equals("groups")) { // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_egg_group%
                                String eggGroups = Arrays.toString(stats.getEggGroups().toArray());
                                eggGroups = eggGroups.substring(1, eggGroups.length()-1);
                                String[] groups = eggGroups.split(", ");
                                if (length == 4) parsed = listFunction(groups, ", ", true); // %pixelmon_party_[1-6]_moves%
                                if (length >= 5 && instructions[4].contains("s:"))
                                    parsed = listFunction(groups, instructionsLeft(4, instructions), true); // %pixelmon_party_[1-6]_moves_s:[separator]%
                            }
                            if(length == 5 && instructions[3].equals("steps") && instructions[4].equals("max")) {
                                // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_egg_steps_max%
                                int stepsPerEggCycle = PixelmonConfigProxy.getBreeding().getStepsPerEggCycle();
                                parsed = String.valueOf((stats.getEggCycles() * stepsPerEggCycle) + stepsPerEggCycle);
                            }
                            break;
                        case "type": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_type…
                            List<String> types = new ArrayList<>();
                            for(Element type : stats.getTypes()) types.add(type.getName());
                            if(length == 3) parsed = (types.size() == 0) ? "None" : listFunction(types.toArray(new String[0]), ", ", true);
                            // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_type%
                            if(length == 4 && instructions[3].contains("s:")) parsed = (types.size() == 0) ? "None" :
                                    listFunction(types.toArray(new String[0]), instructionsLeft(3, instructions), true);
                            // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_type_s:[separator]%
                            if(length == 4 && instructions[3].equals("1")) parsed = (types.size() == 0) ? "None" : types.get(0);
                            // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_type_1%
                            if(length == 4 && instructions[3].equals("2")) parsed = (types.size() < 2) ? "None" : types.get(1);
                            // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_type_2%
                            if(length == 4 && instructions[3].equals("ismono")) parsed = String.valueOf(types.size() == 1);
                            // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_type_isMono%
                            if(length == 5 && instructions[3].equals("has")) {
                                String typeName = instructions[4].substring(0,1).toUpperCase() + instructions[4].substring(1);
                                parsed = String.valueOf(types.contains(typeName));
                                // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_type_has_[type]%
                            }
                            break;
                        case "catch": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_catch_rate%
                            if(length == 4 && instructions[3].equals("rate")) parsed = String.valueOf(stats.getCatchRate());
                            break;
                        case "ev":
                            if(length >= 4 && instructions[3].equals("yield")) { // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_ev_yield…
                                EVYields evYields = stats.getEVYields();
                                if(length == 4) { // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_ev_yield%
                                    int totalYield = 0;
                                    for(BattleStatsType t : ALL_STATS) totalYield += evYields.getYield(t);
                                    parsed = String.valueOf(totalYield);
                                }
                                if(length == 5) switch(instructions[4]) {
                                    case "hp": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_ev_yield_hp%
                                        parsed = String.valueOf(evYields.getYield(BattleStatsType.HP));
                                        break;
                                    case "atk": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_ev_yield_atk%
                                        parsed = String.valueOf(evYields.getYield(BattleStatsType.ATTACK));
                                        break;
                                    case "def": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_ev_yield_def%
                                        parsed = String.valueOf(evYields.getYield(BattleStatsType.DEFENSE));
                                        break;
                                    case "spatk": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_ev_yield_spatk%
                                        parsed = String.valueOf(evYields.getYield(BattleStatsType.SPECIAL_ATTACK));
                                        break;
                                    case "spdef": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_ev_yield_spdef%
                                        parsed = String.valueOf(evYields.getYield(BattleStatsType.SPECIAL_DEFENSE));
                                        break;
                                    case "speed": // %pixelmon_pokedex_[pokemonName,dexNumber]<:formName>_ev_yield_speed%
                                        parsed = String.valueOf(evYields.getYield(BattleStatsType.SPEED));
                                        break;
                                }
                            }
                            break;
                        case "mew":
                        if(length == 5 && instructions[3].equals("clones") && instructions[4].equals("max"))
                            if(species.getName().equals("Mew")) {
                                parsed = String.valueOf(MewStats.MAX_CLONES); // %pixelmon_pokedex_[pokemonName/dexNumber]<:formName>_mew_clones_max%
                            } else {
                                parsed = "Invalid Pokemon.";
                            }
                            break;
                        case "laketrio":
                            if(length == 5 && instructions[3].equals("rubies") && instructions[4].equals("max"))
                                if(LAKE_GUARDIANS.contains(species.getName())) {
                                    // %pixelmon_pokedex_[pokemonName/dexNumber]<:formName>_laketrio_rubies_max%
                                    parsed = String.valueOf(PixelmonConfigProxy.getGeneral().getLakeTrioMaxEnchants());
                                } else {
                                    parsed = "Invalid Pokemon.";
                                }
                            break;
                        case "meltan":
                            if(length == 5 && instructions[3].equals("ores") && instructions[4].equals("max")) {
                                if(species.getName().equals("Meltan")) {
                                    List<Evolution> evolutionData = Objects.requireNonNull(getSpecies("meltan")).getDefaultForm().getEvolutions();
                                    List<OreCondition> oreCondition = evolutionData.get(0).getConditionsOfType(OreCondition.class);
                                    parsed = String.valueOf(oreCondition.get(0).ores); // %pixelmon_pokedex_[pokemonName/dexNumber]<:formName>_meltan_ores_max%
                                } else {
                                    parsed = "Invalid Pokemon.";
                                }
                            }
                            break;
                    }
                    // End Pokedex Placeholders
                }
            }
        }
        if(!(parsed.equals(""))) return parsed;
        return "Invalid Placeholder.";
    }

    private String timeFormat(long preformatted) {
        StringBuilder formatted = new StringBuilder();
        if(preformatted <= 9) {
            formatted.append('0');
        }
        formatted.append(preformatted);
        return formatted.toString();
    }

    private String listFunction(String[] toList, String separator, boolean localized) {
        separator = separator.replaceFirst("s:", "");
        if(toList.length == 1) return toList[0];
        StringBuilder sb = new StringBuilder();
        for (String s : toList) {
            if (!(s.equals("null"))) {
                if (localized) {
                    sb.append(s).append(separator);
                } else {
                    sb.append(unlocalize(s)).append(separator);
                }
            }
        }
        String list = sb.toString();
        return list.substring(0, (list.length() - separator.length()));
    }

    private String unlocalize(String localized) {
        return localized.replaceAll(" ", "");
    }

    private String instructionsLeft(int startPoint, String[] instructions) {
        if(instructions.length == startPoint+1) return instructions[startPoint];
        StringBuilder sb = new StringBuilder();
        for(int i = startPoint; i < instructions.length - 1; i++) {
            sb.append(instructions[i]).append("_");
        }
        sb.append(instructions[instructions.length - 1]);
        return sb.toString();
    }

    private String getHiddenPower(Pokemon pokemon) {
        // Didn't want to fire HiddenPowerCalculateEvent.. and couldn't be asked to give the constructor everything it wanted.
        int[] HADSXY = new int[]{(pokemon.getIVs().getStat(BattleStatsType.HP) % 2),(pokemon.getIVs().getStat(BattleStatsType.ATTACK) % 2),
                (pokemon.getIVs().getStat(BattleStatsType.DEFENSE) % 2),(pokemon.getIVs().getStat(BattleStatsType.SPEED) % 2),
                (pokemon.getIVs().getStat(BattleStatsType.SPECIAL_ATTACK) % 2),(pokemon.getIVs().getStat(BattleStatsType.SPECIAL_DEFENSE) % 2)};
        int odds = 1;
        double hiddenPowerNumber = 0;
        for(int stat : HADSXY) {
            if(stat == 1) {
                hiddenPowerNumber+=odds;
            }
            odds*=2;
        }
        hiddenPowerNumber = Math.floor((hiddenPowerNumber * 15)/63);
        return HIDDEN_POWER_ELEMENTS.get((int) hiddenPowerNumber);
    }

    private Species getSpecies(String toParse) {
        String[] data = toParse.split(":");
        int dexNumber = -1;
        try{
            dexNumber = Integer.parseInt(data[0]);
        } catch (NumberFormatException ignored) {}
        Optional<Species> optional = PixelmonSpecies.fromNameOrDex((dexNumber == -1 ) ? data[0] : dexNumber);
        if(optional.isEmpty()) {
            return null;
        } else {
            return optional.get();
        }
    }

    private String getFormName(String toParse) {
        String[] data = toParse.split(":");
        if(data.length == 1) return "None";
        if(data.length > 1) {
            int dexNumber = -1;
            try{
                dexNumber = Integer.parseInt(data[0]);
            } catch (NumberFormatException ignored) {}
            Optional<Species> optional = PixelmonSpecies.fromNameOrDex((dexNumber == -1 ) ? data[0] : dexNumber);
            if(optional.isEmpty()) return null;
            Stats stats = optional.get().getForm(data[1]);
            if(stats == null) return null;
            if(data.length == 2) return stats.getName();
        }
        return "";
    }

    private int getGeneration(String s) {
        try{
            int gen = Integer.parseInt(s);
            if(gen < 1 || gen > 8) return -1;
            return gen;
        } catch(NumberFormatException e) {
            return -1;
        }
    }

    private String dexPercent(PlayerPokedex dex, int generation, boolean rounded, boolean isInt, boolean caught) {
        int outOf = (generation == 0) ? Pokedex.pokedexSize : PixelmonSpecies.getGenerationDex(generation).size();
        double amount;
        if(caught) {
            amount = (generation == 0) ? dex.countCaught() : dex.countCaught(generation);
        } else {
            amount = (generation == 0) ? dex.countSeen() : dex.countSeen(generation);
        }
        double percent = (amount / outOf) * 100;
        if(rounded) return String.format("%.2f", percent);
        if(isInt) return String.valueOf((int) percent);
        return String.valueOf(percent);
    }
}
