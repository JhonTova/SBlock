package me.sirius.sblock;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.sirius.core.SiriusPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SBlock extends SiriusPlugin implements Listener, TabCompleter {

    private final Map<Enchantment, Integer> blockedEnchantments = new ConcurrentHashMap<>();
    private final Set<UUID> playersToCheck = ConcurrentHashMap.newKeySet();
    private final Map<String, Enchantment> enchantmentNames = new HashMap<>();
    private FileConfiguration config;
    private Map<String, String> messages = new HashMap<>();

    @Override
    protected void onStart() {
        loadConfig();
        listeners(this);
        getCommand("sblock").setTabCompleter(this);
        initializeEnchantmentNames();
        loadBlockedEnchantments();
        startItemCheckTask();
    }

    @Override
    protected void onStop() {
        saveBlockedEnchantments();
    }

    private void loadConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        saveDefaultConfig();
        reloadConfig();
        config = getConfig();
        loadMessages();
    }
    
    private void loadMessages() {
        messages.clear();
        
        messages.put("no_permission", config.getString("messages.no_permission", "&cNo tienes permisos para usar este comando."));
        messages.put("usage", config.getString("messages.usage", "&eUso: /sblock <encantamiento> <nivel|0>"));
        messages.put("examples", config.getString("messages.examples", "&eEjemplos:\n&e  /sblock sharpness 5  - Bloquea Filo V\n&e  /sblock filo 0       - Desbloquea Filo\n&e  /sblock list         - Lista encantamientos bloqueados\n&e  /sblock reload       - Recarga la configuración"));
        messages.put("no_blocked_enchants", config.getString("messages.no_blocked_enchants", "&aNo hay encantamientos bloqueados."));
        messages.put("blocked_enchants_list", config.getString("messages.blocked_enchants_list", "&eEncantamientos bloqueados:"));
        messages.put("enchant_not_found", config.getString("messages.enchant_not_found", "&cEncantamiento no reconocido: {enchant}"));
        messages.put("enchant_unblocked", config.getString("messages.enchant_unblocked", "&aEncantamiento desbloqueado: {enchant}"));
        messages.put("enchant_blocked", config.getString("messages.enchant_blocked", "&cEncantamiento bloqueado: {enchant} (Nivel máximo: {level})"));
        messages.put("enchant_completely_blocked", config.getString("messages.enchant_completely_blocked", "&cEncantamiento completamente bloqueado: {enchant}"));
        messages.put("invalid_level", config.getString("messages.invalid_level", "&cNivel inválido. Debe ser un número entero."));
        messages.put("config_reloaded", config.getString("messages.config_reloaded", "&aConfiguración recargada correctamente!"));
        messages.put("reload_error", config.getString("messages.reload_error", "&cError al recargar la configuración."));
        messages.put("enchant_removed", config.getString("messages.enchant_removed", "&cEl encantamiento &e{enchant} &cfue desactivado de nuestro servidor y fue removido de tus items."));
        messages.put("enchant_reduced", config.getString("messages.enchant_reduced", "&e¡Encantamiento reducido! &a{enchant} &ede nivel &c{old_level} &ea &a{new_level}"));
        messages.put("enchant_blocked_anvil", config.getString("messages.enchant_blocked_anvil", "&cNo puedes agregar el encantamiento &e{enchant} &cporque está bloqueado en el servidor."));
        messages.put("enchant_blocked_table", config.getString("messages.enchant_blocked_table", "&cNo puedes encantar con &e{enchant} &cporque está bloqueado en el servidor."));
    }
    
    private void initializeEnchantmentNames() {
        enchantmentNames.clear();
        enchantmentNames.put("sharpness", getEnchantmentSafe("sharpness"));
        enchantmentNames.put("filo", getEnchantmentSafe("sharpness"));
        enchantmentNames.put("fortune", getEnchantmentSafe("fortune"));
        enchantmentNames.put("fortuna", getEnchantmentSafe("fortune"));
        enchantmentNames.put("efficiency", getEnchantmentSafe("efficiency"));
        enchantmentNames.put("eficiencia", getEnchantmentSafe("efficiency"));
        enchantmentNames.put("protection", getEnchantmentSafe("protection"));
        enchantmentNames.put("proteccion", getEnchantmentSafe("protection"));
        enchantmentNames.put("unbreaking", getEnchantmentSafe("unbreaking"));
        enchantmentNames.put("irrompibilidad", getEnchantmentSafe("unbreaking"));
        enchantmentNames.put("looting", getEnchantmentSafe("looting"));
        enchantmentNames.put("botin", getEnchantmentSafe("looting"));
        enchantmentNames.put("power", getEnchantmentSafe("power"));
        enchantmentNames.put("fuerza", getEnchantmentSafe("power"));
        enchantmentNames.put("punch", getEnchantmentSafe("punch"));
        enchantmentNames.put("empuje", getEnchantmentSafe("punch"));
        enchantmentNames.put("flame", getEnchantmentSafe("flame"));
        enchantmentNames.put("llama", getEnchantmentSafe("flame"));
        enchantmentNames.put("infinity", getEnchantmentSafe("infinity"));
        enchantmentNames.put("infinidad", getEnchantmentSafe("infinity"));
        enchantmentNames.put("knockback", getEnchantmentSafe("knockback"));
        enchantmentNames.put("retroceso", getEnchantmentSafe("knockback"));
        enchantmentNames.put("fire_aspect", getEnchantmentSafe("fire_aspect"));
        enchantmentNames.put("aspecto_igneo", getEnchantmentSafe("fire_aspect"));
        enchantmentNames.put("smite", getEnchantmentSafe("smite"));
        enchantmentNames.put("castigo", getEnchantmentSafe("smite"));
        enchantmentNames.put("bane_of_arthropods", getEnchantmentSafe("bane_of_arthropods"));
        enchantmentNames.put("perdicion_artropodos", getEnchantmentSafe("bane_of_arthropods"));
        enchantmentNames.put("silk_touch", getEnchantmentSafe("silk_touch"));
        enchantmentNames.put("toque_seda", getEnchantmentSafe("silk_touch"));
        enchantmentNames.put("aqua_affinity", getEnchantmentSafe("aqua_affinity"));
        enchantmentNames.put("afinidad_acuatica", getEnchantmentSafe("aqua_affinity"));
        enchantmentNames.put("respiration", getEnchantmentSafe("respiration"));
        enchantmentNames.put("respiracion", getEnchantmentSafe("respiration"));
        enchantmentNames.put("depth_strider", getEnchantmentSafe("depth_strider"));
        enchantmentNames.put("agilidad_acuatica", getEnchantmentSafe("depth_strider"));
        enchantmentNames.put("thorns", getEnchantmentSafe("thorns"));
        enchantmentNames.put("espinas", getEnchantmentSafe("thorns"));
        enchantmentNames.put("frost_walker", getEnchantmentSafe("frost_walker"));
        enchantmentNames.put("paso_helado", getEnchantmentSafe("frost_walker"));
        enchantmentNames.put("mending", getEnchantmentSafe("mending"));
        enchantmentNames.put("reparacion", getEnchantmentSafe("mending"));
        enchantmentNames.put("curse_of_binding", getEnchantmentSafe("binding_curse"));
        enchantmentNames.put("maldicion_ligadura", getEnchantmentSafe("binding_curse"));
        enchantmentNames.put("curse_of_vanishing", getEnchantmentSafe("vanishing_curse"));
        enchantmentNames.put("maldicion_desaparicion", getEnchantmentSafe("vanishing_curse"));
        enchantmentNames.put("maldicion", getEnchantmentSafe("vanishing_curse"));
        
        enchantmentNames.put("sweeping", getEnchantmentSafe("sweeping_edge"));
        enchantmentNames.put("barrido", getEnchantmentSafe("sweeping_edge"));
        enchantmentNames.put("multishot", getEnchantmentSafe("multishot"));
        enchantmentNames.put("multidisparo", getEnchantmentSafe("multishot"));
        enchantmentNames.put("quick_charge", getEnchantmentSafe("quick_charge"));
        enchantmentNames.put("carga_rapida", getEnchantmentSafe("quick_charge"));
        enchantmentNames.put("piercing", getEnchantmentSafe("piercing"));
        enchantmentNames.put("perforacion", getEnchantmentSafe("piercing"));
        enchantmentNames.put("loyalty", getEnchantmentSafe("loyalty"));
        enchantmentNames.put("lealtad", getEnchantmentSafe("loyalty"));
        enchantmentNames.put("impaling", getEnchantmentSafe("impaling"));
        enchantmentNames.put("empalamiento", getEnchantmentSafe("impaling"));
        enchantmentNames.put("riptide", getEnchantmentSafe("riptide"));
        enchantmentNames.put("corriente", getEnchantmentSafe("riptide"));
        enchantmentNames.put("channeling", getEnchantmentSafe("channeling"));
        enchantmentNames.put("canalizar", getEnchantmentSafe("channeling"));
        enchantmentNames.put("soul_speed", getEnchantmentSafe("soul_speed"));
        enchantmentNames.put("velocidad_alma", getEnchantmentSafe("soul_speed"));
        enchantmentNames.put("swift_sneak", getEnchantmentSafe("swift_sneak"));
        enchantmentNames.put("sigilo_rapido", getEnchantmentSafe("swift_sneak"));
    }
    
    private Enchantment getEnchantmentSafe(String enchantmentName) {
        try {
            return org.bukkit.Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchantmentName));
        } catch (Exception e) {
            try {
                return Enchantment.getByKey(NamespacedKey.minecraft(enchantmentName));
            } catch (Exception ex) {
                getLogger().warning("No se pudo cargar el encantamiento: " + enchantmentName);
                return null;
            }
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("sblock") || !sender.hasPermission("sblock.admin")) {
            return new ArrayList<>();
        }
        
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> firstOptions = new ArrayList<>();
            firstOptions.add("list");
            firstOptions.add("reload");
            firstOptions.addAll(enchantmentNames.keySet());
            
            String input = args[0].toLowerCase();
            for (String option : firstOptions) {
                if (option.toLowerCase().startsWith(input)) {
                    completions.add(option);
                }
            }
        } else if (args.length == 2) {
            String enchantName = args[0].toLowerCase();
            if (enchantmentNames.containsKey(enchantName)) {
                List<String> secondOptions = new ArrayList<>();
                secondOptions.add("0");
                secondOptions.add("1");
                secondOptions.add("2");
                secondOptions.add("3");
                secondOptions.add("4");
                secondOptions.add("5");
                secondOptions.add("remove");
                secondOptions.add("unblock");
                secondOptions.add("off");
                
                String input = args[1].toLowerCase();
                for (String option : secondOptions) {
                    if (option.startsWith(input)) {
                        completions.add(option);
                    }
                }
            }
        }
        
        Collections.sort(completions);
        return completions;
    }
    
    private void loadBlockedEnchantments() {
        blockedEnchantments.clear();
        
        if (config.contains("blocked_enchantments")) {
            for (String key : config.getConfigurationSection("blocked_enchantments").getKeys(false)) {
                try {
                    Enchantment enchant = enchantmentNames.get(key.toLowerCase());
                    if (enchant != null) {
                        int level = config.getInt("blocked_enchantments." + key);
                        blockedEnchantments.put(enchant, level);
                    }
                } catch (Exception e) {
                    getLogger().warning("Error cargando encantamiento bloqueado: " + key);
                }
            }
        }
    }
    
    private void saveBlockedEnchantments() {
        config.set("blocked_enchantments", null);
        
        for (Map.Entry<Enchantment, Integer> entry : blockedEnchantments.entrySet()) {
            String enchantName = getEnchantmentConfigName(entry.getKey());
            config.set("blocked_enchantments." + enchantName, entry.getValue());
        }
        
        saveConfig();
    }
    
    private String getEnchantmentConfigName(Enchantment enchantment) {
        for (Map.Entry<String, Enchantment> entry : enchantmentNames.entrySet()) {
            if (entry.getValue() != null && entry.getValue().equals(enchantment)) {
                return entry.getKey();
            }
        }
        return enchantment.getKey().getKey();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("sblock")) {
            return false;
        }
        
        if (!sender.hasPermission("sblock.admin")) {
            send(sender, messages.get("no_permission"));
            return true;
        }
        
        if (args.length == 0) {
            send(sender, messages.get("usage"));
            send(sender, messages.get("examples"));
            return true;
        }
        
        if (args[0].equalsIgnoreCase("reload")) {
            try {
                reloadConfig();
                config = getConfig();
                loadMessages();
                loadBlockedEnchantments();
                
                for (Player player : Bukkit.getOnlinePlayers()) {
                    playersToCheck.add(player.getUniqueId());
                }
                
                send(sender, messages.get("config_reloaded"));
            } catch (Exception e) {
                send(sender, messages.get("reload_error"));
                getLogger().severe("Error recargando configuración: " + e.getMessage());
            }
            return true;
        }
        
        if (args[0].equalsIgnoreCase("list")) {
            if (blockedEnchantments.isEmpty()) {
                send(sender, messages.get("no_blocked_enchants"));
            } else {
                send(sender, messages.get("blocked_enchants_list"));
                for (Map.Entry<Enchantment, Integer> entry : blockedEnchantments.entrySet()) {
                    String name = getEnchantmentDisplayName(entry.getKey());
                    int level = entry.getValue();
                    if (level == 0) {
                        send(sender, "<red>- " + name + " (Completamente bloqueado)");
                    } else {
                        send(sender, "<gray>- " + name + " (Nivel máximo: " + level + ")");
                    }
                }
            }
            return true;
        }
        
        if (args.length < 2) {
            send(sender, messages.get("usage"));
            send(sender, messages.get("examples"));
            return true;
        }
        
        String enchantmentName = args[0].toLowerCase();
        
        try {
            int level = Integer.parseInt(args[1]);
            
            Enchantment enchantment = enchantmentNames.get(enchantmentName);
            if (enchantment == null) {
                send(sender, messages.get("enchant_not_found").replace("{enchant}", enchantmentName));
                return true;
            }
            
            if (level < 0) {
                send(sender, messages.get("invalid_level"));
                return true;
            }
            
            if (blockedEnchantments.containsKey(enchantment) && blockedEnchantments.get(enchantment) == level) {
                return true;
            }
            
            if (level == 0) {
                blockedEnchantments.put(enchantment, 0);
                saveBlockedEnchantments();
                send(sender, messages.get("enchant_completely_blocked").replace("{enchant}", getEnchantmentDisplayName(enchantment)));
            } else {
                boolean wasCompletelyBlocked = blockedEnchantments.containsKey(enchantment) && blockedEnchantments.get(enchantment) == 0;
                
                if (wasCompletelyBlocked) {
                    blockedEnchantments.put(enchantment, level);
                    saveBlockedEnchantments();
                    send(sender, messages.get("enchant_blocked")
                        .replace("{enchant}", getEnchantmentDisplayName(enchantment))
                        .replace("{level}", String.valueOf(level)));
                } else if (blockedEnchantments.containsKey(enchantment)) {
                    blockedEnchantments.put(enchantment, level);
                    saveBlockedEnchantments();
                    send(sender, messages.get("enchant_blocked")
                        .replace("{enchant}", getEnchantmentDisplayName(enchantment))
                        .replace("{level}", String.valueOf(level)));
                } else {
                    blockedEnchantments.put(enchantment, level);
                    saveBlockedEnchantments();
                    send(sender, messages.get("enchant_blocked")
                        .replace("{enchant}", getEnchantmentDisplayName(enchantment))
                        .replace("{level}", String.valueOf(level)));
                }
            }
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                playersToCheck.add(player.getUniqueId());
            }
            
        } catch (NumberFormatException e) {
            if (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("unblock") || args[1].equalsIgnoreCase("off")) {
                Enchantment enchantment = enchantmentNames.get(enchantmentName);
                if (enchantment == null) {
                    send(sender, messages.get("enchant_not_found").replace("{enchant}", enchantmentName));
                    return true;
                }
                
                blockedEnchantments.remove(enchantment);
                saveBlockedEnchantments();
                send(sender, messages.get("enchant_unblocked").replace("{enchant}", getEnchantmentDisplayName(enchantment)));
            } else {
                send(sender, messages.get("invalid_level"));
            }
        }
        
        return true;
    }
    
    private String getEnchantmentDisplayName(Enchantment enchantment) {
        if (enchantment == null) return "Unknown";
        String key = enchantment.getKey().getKey();
        return key.substring(0, 1).toUpperCase() + key.substring(1).replace("_", " ");
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        queue(event.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        queue(event.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            queue(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playersToCheck.remove(event.getPlayer().getUniqueId());
    }

    // Sin nada bloqueado no hay nada que revisar: encolar igualmente dejaba crecer
    // el conjunto con cada jugador que pasaba por el servidor.
    private void queue(Player player) {
        if (!blockedEnchantments.isEmpty()) {
            playersToCheck.add(player.getUniqueId());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnchantItem(EnchantItemEvent event) {
        Map<Enchantment, Integer> enchants = event.getEnchantsToAdd();
        Player player = event.getEnchanter();
        
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment enchant = entry.getKey();
            int level = entry.getValue();
            
            if (blockedEnchantments.containsKey(enchant)) {
                int maxLevel = blockedEnchantments.get(enchant);
                
                if (maxLevel == 0) {
                    event.setCancelled(true);
                    send(player, messages.get("enchant_blocked_table")
                        .replace("{enchant}", getEnchantmentDisplayName(enchant)));
                    return;
                } else if (level > maxLevel) {
                    event.setCancelled(true);
                    send(player, messages.get("enchant_blocked_table")
                        .replace("{enchant}", getEnchantmentDisplayName(enchant) + " " + level));
                    return;
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();
        if (result == null || result.getType() == Material.AIR) return;
        
        ItemMeta meta = result.getItemMeta();
        if (meta == null || !meta.hasEnchants()) return;
        
        Map<Enchantment, Integer> enchants = meta.getEnchants();
        boolean blocked = false;
        
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment enchant = entry.getKey();
            int level = entry.getValue();
            
            if (blockedEnchantments.containsKey(enchant)) {
                int maxLevel = blockedEnchantments.get(enchant);
                
                if (maxLevel == 0 || level > maxLevel) {
                    blocked = true;
                    break;
                }
            }
        }
        
        if (blocked) {
            event.setResult(null);
            if (event.getViewers().size() > 0 && event.getViewers().get(0) instanceof Player) {
                Player player = (Player) event.getViewers().get(0);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        send(player, messages.get("enchant_blocked_anvil")
                            .replace("{enchant}", "uno o más encantamientos"));
                    }
                }.runTaskLater(this, 1L);
            }
        }
    }
    
    private void startItemCheckTask() {
        scheduler().syncTimer(() -> {
            if (playersToCheck.isEmpty()) {
                return;
            }
            if (blockedEnchantments.isEmpty()) {
                playersToCheck.clear();
                return;
            }
            Set<UUID> toCheck = new HashSet<>(playersToCheck);
            playersToCheck.clear();

            for (UUID uuid : toCheck) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    checkPlayerItems(player);
                }
            }
        }, 20L, 60L);
    }
    
    private void checkPlayerItems(Player player) {
        ItemStack[] contents = player.getInventory().getContents();

        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack item = contents[slot];
            // getEnchantments() lee el item sin copiar su meta; getItemMeta() la clona
            // entera, y la mayoria del inventario no lleva encantamientos.
            if (item == null || item.getType() == Material.AIR || item.getEnchantments().isEmpty()) {
                continue;
            }

            EnchantmentResult result = processBlockedEnchantments(item);
            if (!result.modified) {
                continue;
            }

            player.getInventory().setItem(slot, result.item);

            for (String enchantName : result.removedEnchants) {
                send(player, messages.get("enchant_removed").replace("{enchant}", enchantName));
            }
            for (EnchantmentReduction reduction : result.reducedEnchants) {
                send(player, messages.get("enchant_reduced")
                        .replace("{enchant}", reduction.enchantName)
                        .replace("{old_level}", String.valueOf(reduction.oldLevel))
                        .replace("{new_level}", String.valueOf(reduction.newLevel))
                        .replace("{level}", String.valueOf(reduction.newLevel)));
            }
        }
    }
    
    private EnchantmentResult processBlockedEnchantments(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasEnchants()) {
            return new EnchantmentResult(item, false, new ArrayList<>(), new ArrayList<>());
        }
        
        boolean modified = false;
        Map<Enchantment, Integer> enchants = new HashMap<>(meta.getEnchants());
        List<String> removedEnchants = new ArrayList<>();
        List<EnchantmentReduction> reducedEnchants = new ArrayList<>();
        
        for (Map.Entry<Enchantment, Integer> blockedEntry : blockedEnchantments.entrySet()) {
            Enchantment blockedEnchant = blockedEntry.getKey();
            int maxLevel = blockedEntry.getValue();
            
            if (enchants.containsKey(blockedEnchant)) {
                int currentLevel = enchants.get(blockedEnchant);
                
                if (maxLevel == 0) {
                    meta.removeEnchant(blockedEnchant);
                    modified = true;
                    removedEnchants.add(getEnchantmentDisplayName(blockedEnchant));
                } else if (currentLevel > maxLevel) {
                    meta.removeEnchant(blockedEnchant);
                    meta.addEnchant(blockedEnchant, maxLevel, true);
                    modified = true;
                    reducedEnchants.add(new EnchantmentReduction(
                        getEnchantmentDisplayName(blockedEnchant), 
                        currentLevel, 
                        maxLevel
                    ));
                }
            }
        }
        
        if (modified) {
            ItemStack newItem = item.clone();
            newItem.setItemMeta(meta);
            return new EnchantmentResult(newItem, true, removedEnchants, reducedEnchants);
        }
        
        return new EnchantmentResult(item, false, removedEnchants, reducedEnchants);
    }

    private static class EnchantmentReduction {
        final String enchantName;
        final int oldLevel;
        final int newLevel;
        
        EnchantmentReduction(String enchantName, int oldLevel, int newLevel) {
            this.enchantName = enchantName;
            this.oldLevel = oldLevel;
            this.newLevel = newLevel;
        }
    }
    
    private static class EnchantmentResult {
        final ItemStack item;
        final boolean modified;
        final List<String> removedEnchants;
        final List<EnchantmentReduction> reducedEnchants;
        
        EnchantmentResult(ItemStack item, boolean modified, List<String> removedEnchants, List<EnchantmentReduction> reducedEnchants) {
            this.item = item;
            this.modified = modified;
            this.removedEnchants = removedEnchants;
            this.reducedEnchants = reducedEnchants;
        }
    }
}