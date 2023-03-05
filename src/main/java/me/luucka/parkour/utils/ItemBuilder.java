package me.luucka.parkour.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * ItemBuilder class help you to create {@link ItemStack} easily
 */
public class ItemBuilder {

    private final Material material;
    private final ItemMeta meta;
    private final int amount;

    /**
     * Constructor
     *
     * @param material {@link Material} representing the item
     */
    public ItemBuilder(final Material material) {
        this(material, 1);
    }

    /**
     * Constructor
     *
     * @param material {@link Material} representing the item
     * @param amount   of the items will give you
     */
    public ItemBuilder(final Material material, final int amount) {
        this.material = material;
        this.meta = Bukkit.getItemFactory().getItemMeta(material);
        this.amount = amount <= 0 ? 1 : amount;
    }

    /**
     * Return a new {@link ItemStack} based on 'material' and 'amount'
     *
     * @return an {@link ItemStack}
     */
    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(this.material, this.amount);
        item.setItemMeta(this.meta);
        return item;
    }

    /*
        Generic Item section
        ----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Set item display name.
     *
     * @param name the name to set
     */
    public ItemBuilder setDisplayName(final Component name) {
        meta.displayName(name);
        return this;
    }

    /**
     * Sets the lore for this item.
     * Removes lore when given null.
     *
     * @param lore the lore that will be set
     */
    public ItemBuilder setLore(final List<Component> lore) {
        meta.lore(lore);
        return this;
    }

    /**
     * Sets the lore for this item.
     * Removes lore when given null.
     *
     * @param lore the lore that will be set
     */
    public ItemBuilder setLore(final Component... lore) {
        return setLore(Arrays.asList(lore));
    }

    public ItemBuilder setDamage(final int damage) {
        ((Damageable) meta).setDamage(damage);
        return this;
    }

    /**
     * Adds the specified enchantments to this item stack.
     * <p>
     * This method is the same as calling {@link
     * #addEnchantment(Enchantment, int)} for each
     * element of the map.
     *
     * @param enchantments Enchantments to add
     */
    public ItemBuilder addEnchantments(final Map<Enchantment, Integer> enchantments) {
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            _addEnchant(entry.getKey(), entry.getValue(), false);
        }
        return this;
    }

    /**
     * Adds the specified {@link Enchantment} to this item stack.
     * <p>
     * If this item stack already contained the given enchantment (at any
     * level), it will be replaced.
     *
     * @param enchantment Enchantment to add
     * @param level       Level of the enchantment
     */
    public ItemBuilder addEnchantment(final Enchantment enchantment, final int level) {
        _addEnchant(enchantment, level, false);
        return this;
    }

    /**
     * Adds the specified enchantments to this item stack in an unsafe manner.
     * <p>
     * This method is the same as calling {@link
     * #addUnsafeEnchantment(Enchantment, int)} for
     * each element of the map.
     *
     * @param enchantments Enchantments to add
     */
    public ItemBuilder addUnsafeEnchantments(final Map<Enchantment, Integer> enchantments) {
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            _addEnchant(entry.getKey(), entry.getValue(), true);
        }
        return this;
    }

    /**
     * Adds the specified {@link Enchantment} to this item stack.
     * <p>
     * If this item stack already contained the given enchantment (at any
     * level), it will be replaced.
     * <p>
     * This method is unsafe and will ignore level restrictions or item type.
     * Use at your own discretion.
     *
     * @param enchantment Enchantment to add
     * @param level       Level of the enchantment
     */
    public ItemBuilder addUnsafeEnchantment(final Enchantment enchantment, final int level) {
        _addEnchant(enchantment, level, true);
        return this;
    }

    /**
     * Adds the specified enchantment to this item meta.
     *
     * @param enchantment            Enchantment to add
     * @param level                  Level for the enchantment
     * @param ignoreLevelRestriction this indicates the enchantment should be
     *                               applied, ignoring the level limit
     */
    private void _addEnchant(final Enchantment enchantment, final int level, final boolean ignoreLevelRestriction) {
        meta.addEnchant(enchantment, level, ignoreLevelRestriction);
    }

    /**
     * Set a persistent data container value into this item
     *
     * @param plugin your plugin class extends {@link JavaPlugin}
     * @param key    a key reference for your value
     * @param value  value
     */
    public ItemBuilder setPersistentDataContainerValue(final JavaPlugin plugin, final String key, final String value) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);
        return this;
    }

    /**
     * Set itemflags which should be ignored when rendering a ItemStack in the Client.
     * <p>
     * This Method does silently ignore double set itemFlags.
     *
     * @param flags The hideflags which shouldn't be rendered
     */
    public ItemBuilder addItemFlags(final ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    /**
     * Set itemflags which should be ignored when rendering a ItemStack in the Client.
     * <p>
     * This Method does silently ignore double set itemFlags.
     */
    public ItemBuilder hideEnchants() {
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    /**
     * Set itemflags which should be ignored when rendering a ItemStack in the Client.
     * <p>
     * This Method does silently ignore double set itemFlags.
     */
    public ItemBuilder hideAttributes() {
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        return this;
    }

    /**
     * Set itemflags which should be ignored when rendering a ItemStack in the Client.
     * <p>
     * This Method does silently ignore double set itemFlags.
     */
    public ItemBuilder hideUnbreakable() {
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        return this;
    }

    /**
     * Set itemflags which should be ignored when rendering a ItemStack in the Client.
     * <p>
     * This Method does silently ignore double set itemFlags.
     */
    public ItemBuilder hideDestroys() {
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        return this;
    }

    /**
     * Set itemflags which should be ignored when rendering a ItemStack in the Client.
     * <p>
     * This Method does silently ignore double set itemFlags.
     */
    public ItemBuilder hidePlacedOn() {
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        return this;
    }

    /**
     * Set itemflags which should be ignored when rendering a ItemStack in the Client.
     * <p>
     * This Method does silently ignore double set itemFlags.
     */
    public ItemBuilder hidePotionEffects() {
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        return this;
    }

    /**
     * Set itemflags which should be ignored when rendering a ItemStack in the Client.
     * <p>
     * This Method does silently ignore double set itemFlags.
     */
    public ItemBuilder hideDye() {
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        return this;
    }

    /**
     * Sets the unbreakable tag. An unbreakable item will not lose durability.
     *
     * @param unbreakable true if set unbreakable
     */
    public ItemBuilder setUnbreakable(final boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    /**
     * Sets the repair penalty
     *
     * @param cost repair penalty
     */
    public ItemBuilder setRepairCost(final int cost) {
        ((Repairable) meta).setRepairCost(cost);
        return this;
    }

    /*
        AxolotlBucketMeta section
        ----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Set the variant of this axolotl in the bucket.
     *
     * @param variant axolotl variant
     */
    public ItemBuilder setAxolotlVariant(final Axolotl.Variant variant) {
        if (meta instanceof AxolotlBucketMeta axolotlBucketMeta) {
            axolotlBucketMeta.setVariant(variant);
        }
        return this;
    }

    /*
        BannerMeta section
        ----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Sets the patterns used on this banner
     *
     * @param patterns the new list of patterns
     */
    public ItemBuilder setBannerPatterns(final List<Pattern> patterns) {
        if (meta instanceof BannerMeta bannerMeta) {
            bannerMeta.setPatterns(patterns);
        }
        return this;
    }

    /**
     * Adds a new pattern on top of the existing
     * patterns
     *
     * @param pattern the new pattern to add
     */
    public ItemBuilder addBannerPattern(final Pattern pattern) {
        if (meta instanceof BannerMeta bannerMeta) {
            bannerMeta.addPattern(pattern);
        }
        return this;
    }

    /**
     * Sets the pattern at the specified index
     *
     * @param i       the index
     * @param pattern the new pattern
     * @throws IndexOutOfBoundsException when index is not in [0, numberOfPatterns()) range
     */
    public ItemBuilder setBannerPattern(final int i, final Pattern pattern) {
        if (meta instanceof BannerMeta bannerMeta) {
            bannerMeta.setPattern(i, pattern);
        }
        return this;
    }

    /*
        BookMeta section
        ----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Sets the title of the book.
     * <p>
     * Limited to 32 characters. Removes title when given null.
     *
     * @param title the title to set
     */
    public ItemBuilder setBookTitle(final Component title) {
        if (meta instanceof BookMeta bookMeta) {
            bookMeta.title(title);
        }
        return this;
    }

    /**
     * Sets the author of the book. Removes author when given null.
     *
     * @param author the author to set
     */
    public ItemBuilder setBookAuthor(final Component author) {
        if (meta instanceof BookMeta bookMeta) {
            bookMeta.author(author);
        }
        return this;
    }

    /**
     * Sets the specified page in the book. Pages of the book must be
     * contiguous.
     * <p>
     * The data can be up to 256 characters in length, additional characters
     * are truncated.
     * <p>
     * Pages are 1-indexed.
     *
     * @param page the page number to set, in range [1, getPageCount()]
     * @param data the data to set for that page
     */
    public ItemBuilder setBookPage(final int page, final Component data) {
        if (meta instanceof BookMeta bookMeta) {
            bookMeta.page(page, data);
        }
        return this;
    }

    /**
     * Clears the existing book pages, and sets the book to use the provided
     * pages. Maximum 100 pages with 256 characters per page.
     *
     * @param pages A list of pages to set the book to use
     */
    public ItemBuilder setBookPages(final List<String> pages) {
        if (meta instanceof BookMeta bookMeta) {
            bookMeta.setPages(pages);
        }
        return this;
    }

    /**
     * Clears the existing book pages, and sets the book to use the provided
     * pages. Maximum 100 pages with 256 characters per page.
     *
     * @param pages A list of pages to set the book to use
     */
    public ItemBuilder setBookPages(final String... pages) {
        return setBookPages(Arrays.asList(pages));
    }

    /**
     * Adds new pages to the end of the book. Up to a maximum of 50 pages with
     * 256 characters per page.
     *
     * @param pages A list of strings, each being a page
     */
    public ItemBuilder addBookPage(final String... pages) {
        if (meta instanceof BookMeta bookMeta) {
            bookMeta.addPage(pages);
        }
        return this;
    }

    /**
     * Sets the generation of the book. Removes generation when given null.
     *
     * @param generation the generation to set
     */
    public ItemBuilder setBookGeneration(final BookMeta.Generation generation) {
        if (meta instanceof BookMeta bookMeta) {
            bookMeta.setGeneration(generation);
        }
        return this;
    }

    /*
        BundleMeta section
        ----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Sets the items stored in this item.
     * <p>
     * Removes all items when given null.
     *
     * @param items the items to set
     */
    public ItemBuilder setBundleItems(final List<ItemStack> items) {
        if (meta instanceof BundleMeta bundleMeta) {
            bundleMeta.setItems(items);
        }
        return this;
    }

    /**
     * Sets the items stored in this item.
     * <p>
     * Removes all items when given null.
     *
     * @param items the items to set
     */
    public ItemBuilder setBundleItems(final ItemStack... items) {
        return setBundleItems(Arrays.asList(items));
    }

    /**
     * Adds an item to this item.
     *
     * @param item item to add
     */
    public ItemBuilder addBundleItem(final ItemStack item) {
        if (meta instanceof BundleMeta bundleMeta) {
            bundleMeta.addItem(item);
        }
        return this;
    }

    /*
       CompassMeta section
        ----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Sets the location this lodestone compass will point to.
     *
     * @param lodestone new location or null to clear
     */
    public ItemBuilder setCompassLodestone(final Location lodestone) {
        if (meta instanceof CompassMeta compassMeta) {
            compassMeta.setLodestone(lodestone);
        }
        return this;
    }

    /**
     * Sets if this compass is tracking a specific lodestone.
     * <p>
     * If true the compass will only work if there is a lodestone at the tracked
     * location.
     *
     * @param tracked new tracked status
     */
    public ItemBuilder setCompassLodestoneTracked(final boolean tracked) {
        if (meta instanceof CompassMeta compassMeta) {
            compassMeta.setLodestoneTracked(tracked);
        }
        return this;
    }

    /*
        CrossbowMeta section
        ----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Sets the projectiles charged on this item.
     * <p>
     * Removes all projectiles when given null.
     *
     * @param projectiles the projectiles to set
     * @throws IllegalArgumentException if one of the projectiles is not an
     *                                  arrow or firework rocket
     */
    public ItemBuilder setCrossbowChargedProjectiles(final List<ItemStack> projectiles) {
        if (meta instanceof CrossbowMeta crossbowMeta) {
            crossbowMeta.setChargedProjectiles(projectiles);
        }
        return this;
    }

    /**
     * Sets the projectiles charged on this item.
     * <p>
     * Removes all projectiles when given null.
     *
     * @param projectiles the projectiles to set
     * @throws IllegalArgumentException if one of the projectiles is not an
     *                                  arrow or firework rocket
     */
    public ItemBuilder setCrossbowChargedProjectiles(final ItemStack... projectiles) {
        return setCrossbowChargedProjectiles(Arrays.asList(projectiles));
    }

    /**
     * Adds a charged projectile to this item.
     *
     * @param projectile projectile
     * @throws IllegalArgumentException if the projectile is not an arrow or
     *                                  firework rocket
     */
    public ItemBuilder addCrossbowChargedProjectile(final ItemStack projectile) {
        if (meta instanceof CrossbowMeta crossbowMeta) {
            crossbowMeta.addChargedProjectile(projectile);
        }
        return this;
    }

    /*
        EnchantmentStorageMeta section
        ----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Stores the specified enchantment in this item.
     *
     * @param enchantment Enchantment to store
     * @param level       Level for the enchantment
     * @throws IllegalArgumentException if enchantment is null
     */
    public ItemBuilder addEnchantedBookStoredEnchant(final Enchantment enchantment, final int level) {
        _addEnchantedBookStoredEnchant(enchantment, level, false);
        return this;
    }

    /**
     * Stores the specified unsafe enchantment in this item.
     *
     * @param enchantment Enchantment to store
     * @param level       Level for the enchantment
     * @throws IllegalArgumentException if enchantment is null
     */
    public ItemBuilder addEnchantedBookStoredUnsafeEnchant(final Enchantment enchantment, final int level) {
        _addEnchantedBookStoredEnchant(enchantment, level, true);
        return this;
    }

    /**
     * Stores the specified enchantment in this item.
     *
     * @param enchantment            Enchantment to store
     * @param level                  Level for the enchantment
     * @param ignoreLevelRestriction this indicates the enchantment should be
     *                               applied, ignoring the level limit
     * @throws IllegalArgumentException if enchantment is null
     */
    private void _addEnchantedBookStoredEnchant(final Enchantment enchantment, final int level, final boolean ignoreLevelRestriction) {
        if (meta instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
            enchantmentStorageMeta.addStoredEnchant(enchantment, level, ignoreLevelRestriction);
        }
    }

    /*
        FireworkEffectMeta section
        ----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Sets the firework effect for this meta.
     *
     * @param effect the effect to set, or null to indicate none.
     */
    public ItemBuilder setFireworkChargeEffect(final FireworkEffect effect) {
        if (meta instanceof FireworkEffectMeta fireworkEffectMeta) {
            fireworkEffectMeta.setEffect(effect);
        }
        return this;
    }

    /*
        FireworkMeta section
        ----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Add another effect to this firework.
     *
     * @param effect The firework effect to add
     * @throws IllegalArgumentException If effect is null
     */
    public ItemBuilder addFireworkEffect(final FireworkEffect effect) {
        if (meta instanceof FireworkMeta fireworkMeta) {
            fireworkMeta.addEffect(effect);
        }
        return this;
    }

    /**
     * Add several effects to this firework.
     *
     * @param effects The firework effects to add
     * @throws IllegalArgumentException If effects is null
     * @throws IllegalArgumentException If any effect is null (may be thrown
     *                                  after changes have occurred)
     */
    public ItemBuilder addFireworkEffects(final FireworkEffect... effects) {
        if (meta instanceof FireworkMeta fireworkMeta) {
            fireworkMeta.addEffects(effects);
        }
        return this;
    }

    /**
     * Sets the approximate power of the firework. Each level of power is half
     * a second of flight time.
     *
     * @param power the power of the firework, from 0-127
     * @throws IllegalArgumentException if {@literal height<0 or height>127}
     */
    public ItemBuilder setFireworkPower(final int power) {
        if (meta instanceof FireworkMeta fireworkMeta) {
            fireworkMeta.setPower(power);
        }
        return this;
    }

    /*
        KnowledgeBookMeta section
        ----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Clears the existing book recipes, and sets the book to use the provided
     * recipes.
     *
     * @param recipes A list of recipes to set the book to use
     */
    public ItemBuilder setKnowledgeBookRecipes(final List<NamespacedKey> recipes) {
        if (meta instanceof KnowledgeBookMeta knowledgeBookMeta) {
            knowledgeBookMeta.setRecipes(recipes);
        }
        return this;
    }

    /**
     * Adds new recipe to the end of the book.
     *
     * @param recipes A list of recipe keys
     */
    public ItemBuilder addKnowledgeBookRecipe(final NamespacedKey... recipes) {
        if (meta instanceof KnowledgeBookMeta knowledgeBookMeta) {
            knowledgeBookMeta.addRecipe(recipes);
        }
        return this;
    }

    /*
        LeatherArmorMeta section
        ----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Sets the color of the armor.
     *
     * @param color the color to set. Setting it to null is equivalent to
     *              setting it to {@link ItemFactory#getDefaultLeatherColor()}.
     */
    public ItemBuilder setLeatherArmorColor(Color color) {
        if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
            leatherArmorMeta.setColor(color);
        }
        return this;
    }

    /**
     * Sets the color of the armor from RGB.
     *
     * @param red   RGB red value
     * @param green RGB green value
     * @param blue  RGB blue value
     */
    public ItemBuilder setLeatherArmorColor(final int red, final int green, final int blue) {
        return setLeatherArmorColor(Color.fromRGB(red, green, blue));
    }

    /*
        MapMeta section
        ----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Sets the associated map. This is used to determine what map is displayed.
     *
     * <p>
     * The implementation <b>may</b> allow null to clear the associated map, but
     * this is not required and is liable to generate a new (undefined) map when
     * the item is first used.
     *
     * @param map the map to set
     */
    public ItemBuilder setMapView(final MapView map) {
        if (meta instanceof MapMeta mapMeta) {
            mapMeta.setMapView(map);
        }
        return this;
    }

    /**
     * Sets if this map is scaling or not.
     *
     * @param value true to scale
     */
    public ItemBuilder setMapScaling(final boolean value) {
        if (meta instanceof MapMeta mapMeta) {
            mapMeta.setScaling(value);
        }
        return this;
    }

    /**
     * Sets the location name. A custom map color will alter the display of the
     * map in an inventory slot.
     *
     * @param name the name to set
     */
    public ItemBuilder setMapLocationName(final String name) {
        if (meta instanceof MapMeta mapMeta) {
            mapMeta.setLocationName(name);
        }
        return this;
    }

    /**
     * Sets the map color. A custom map color will alter the display of the map
     * in an inventory slot.
     *
     * @param color the color to set
     */
    public ItemBuilder setMapColor(final Color color) {
        if (meta instanceof MapMeta mapMeta) {
            mapMeta.setColor(color);
        }
        return this;
    }

    /**
     * Sets the map color. A custom map color will alter the display of the map
     * in an inventory slot.
     *
     * @param red   RGB red value
     * @param green RGB green value
     * @param blue  RGB blue value
     */
    public ItemBuilder setMapColor(final int red, final int green, final int blue) {
        return setMapColor(Color.fromRGB(red, green, blue));
    }

    /*
        MusicInstrumentMeta section
        ----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Sets the goat horn's instrument.
     *
     * @param instrument the instrument to set
     */
    public ItemBuilder setMusicInstrument(final MusicInstrument instrument) {
        if (meta instanceof MusicInstrumentMeta musicInstrumentMeta) {
            musicInstrumentMeta.setInstrument(instrument);
        }
        return this;
    }

    /*
        PotionMeta section
        ----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Sets the underlying potion data
     *
     * @param potionType the type of the Potion
     * @param extended   whether the potion is extended PotionType#isExtendable()
     *                   must be true
     * @param upgraded   whether the potion is upgraded PotionType#isUpgradable()
     *                   must be true
     */
    public ItemBuilder setBasePotionData(final PotionType potionType, final boolean extended, final boolean upgraded) {
        if (meta instanceof PotionMeta potionMeta && (!extended || !upgraded)) {
            potionMeta.setBasePotionData(new PotionData(potionType, potionType.isExtendable() && extended, potionType.isUpgradeable() && upgraded));
        }
        return this;
    }

    /**
     * Adds a custom potion effect to this potion.
     *
     * @param effect    the potion effect to add
     * @param overwrite true if any existing effect of the same type should be
     *                  overwritten
     */
    public ItemBuilder addPotionCustomEffect(final PotionEffect effect, boolean overwrite) {
        if (meta instanceof PotionMeta potionMeta) {
            potionMeta.addCustomEffect(effect, overwrite);
        }
        return this;
    }

    /**
     * Adds a custom potion effect to this potion.
     *
     * @param type      effect type
     * @param duration  measured in ticks, see {@link PotionEffect#getDuration()}
     * @param amplifier the amplifier, see {@link PotionEffect#getAmplifier()}
     * @param ambient   the ambient status, see {@link PotionEffect#isAmbient()}
     * @param particles the particle status, see {@link PotionEffect#hasParticles()}
     * @param icon      the icon status, see {@link PotionEffect#hasIcon()}
     * @param overwrite true if any existing effect of the same type should be overwritten
     */
    public ItemBuilder addPotionCustomEffect(final PotionEffectType type, final int duration, final int amplifier, final boolean ambient, final boolean particles, final boolean icon, final boolean overwrite) {
        return addPotionCustomEffect(new PotionEffect(type, duration, amplifier, ambient, particles, icon), overwrite);
    }

    /**
     * Sets the potion color. A custom potion color will alter the display of
     * the potion in an inventory slot.
     *
     * @param color the color to set
     */
    public ItemBuilder setPotionColor(final Color color) {
        if (meta instanceof PotionMeta potionMeta) {
            potionMeta.setColor(color);
        }
        return this;
    }

    /**
     * Sets the potion color. A custom potion color will alter the display of
     * the potion in an inventory slot.
     *
     * @param red   RGB red value
     * @param green RGB green value
     * @param blue  RGB blue value
     */
    public ItemBuilder setPotionColor(final int red, final int green, final int blue) {
        return setPotionColor(Color.fromRGB(red, green, blue));
    }

    /*
        SkullMeta section
        ----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Sets this skull to use the supplied Player Profile, which can include textures already prefilled.
     *
     * @param profile The profile to set this Skull to use, or null to clear owner
     */
    public ItemBuilder setSkullPlayerProfile(final PlayerProfile profile) {
        if (meta instanceof SkullMeta skullMeta) {
            skullMeta.setPlayerProfile(profile);
        }
        return this;
    }

    /**
     * Sets the owner of the skull.
     * <p>
     * Plugins should check that hasOwner() returns true before calling this
     * plugin.
     *
     * @param owner the new owner of the skull
     * @return true if the owner was successfully set
     */
    public ItemBuilder setSkullOwningPlayer(final OfflinePlayer owner) {
        if (meta instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(owner);
        }
        return this;
    }

    /*
        SuspiciousStewMeta section
        ----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Adds a custom potion effect to this suspicious stew.
     *
     * @param effect    the potion effect to add
     * @param overwrite true if any existing effect of the same type should be
     *                  overwritten
     */
    public ItemBuilder addSuspiciousStewCustomEffect(final PotionEffect effect, boolean overwrite) {
        if (meta instanceof SuspiciousStewMeta suspiciousStewMeta) {
            suspiciousStewMeta.addCustomEffect(effect, overwrite);
        }
        return this;
    }

    /**
     * Adds a custom potion effect to this suspicious stew.
     *
     * @param type      effect type
     * @param duration  measured in ticks, see {@link PotionEffect#getDuration()}
     * @param amplifier the amplifier, see {@link PotionEffect#getAmplifier()}
     * @param ambient   the ambient status, see {@link PotionEffect#isAmbient()}
     * @param particles the particle status, see {@link PotionEffect#hasParticles()}
     * @param icon      the icon status, see {@link PotionEffect#hasIcon()}
     * @param overwrite true if any existing effect of the same type should be overwritten
     */
    public ItemBuilder addSuspiciousStewCustomEffect(final PotionEffectType type, final int duration, final int amplifier, final boolean ambient, final boolean particles, final boolean icon, final boolean overwrite) {
        return addSuspiciousStewCustomEffect(new PotionEffect(type, duration, amplifier, ambient, particles, icon), overwrite);
    }

    /*
        TropicalFishBucketMeta section
        ----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Sets the color of the fish's pattern.
     * <p>
     * Setting this when hasVariant() returns <code>false</code> will initialize
     * all other values to unspecified defaults.
     *
     * @param color pattern color
     */
    public ItemBuilder setTropicalFishBucketPatternColor(final DyeColor color) {
        if (meta instanceof TropicalFishBucketMeta tropicalFishBucketMeta) {
            tropicalFishBucketMeta.setPatternColor(color);
        }
        return this;
    }

    /**
     * Sets the color of the fish's body.
     * <p>
     * Setting this when hasVariant() returns <code>false</code> will initialize
     * all other values to unspecified defaults.
     *
     * @param color body color
     */
    public ItemBuilder setTropicalFishBucketBodyColor(final DyeColor color) {
        if (meta instanceof TropicalFishBucketMeta tropicalFishBucketMeta) {
            tropicalFishBucketMeta.setBodyColor(color);
        }
        return this;
    }

    /**
     * Sets the fish's pattern.
     * <p>
     * Setting this when hasVariant() returns <code>false</code> will initialize
     * all other values to unspecified defaults.
     *
     * @param pattern new pattern
     */
    public ItemBuilder setTropicalFishBucketPattern(final TropicalFish.Pattern pattern) {
        if (meta instanceof TropicalFishBucketMeta tropicalFishBucketMeta) {
            tropicalFishBucketMeta.setPattern(pattern);
        }
        return this;
    }

}
