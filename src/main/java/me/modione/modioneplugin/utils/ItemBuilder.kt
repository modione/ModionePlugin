/* Copyright 2016 Acquized
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.modione.modioneplugin.utils

import com.google.gson.Gson
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.apache.commons.lang3.Validate
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.material.MaterialData
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * ItemBuilder - API Class to create a [org.bukkit.inventory.ItemStack] with just one line of Code
 *
 * @author Acquized
 * @version 1.8
 * @contributor Kev575
 */
class ItemBuilder {
    private var item: ItemStack?

    /**
     * Returns the ItemMeta
     */
    var meta: ItemMeta? = null
        private set

    /**
     * Returns the Material
     */
    var material: Material?
        private set

    /**
     * Returns the Amount
     */
    var amount = 1
        private set

    /**
     * Returns the MaterialData
     */
    var data: MaterialData? = null
        private set
    /**
     * Returns the Damage
     *
     */
    /**
     * Returns the Durability
     */
    @get:Deprecated("Use {@code ItemBuilder#getDurability}")
    var durability: Short = 0
        private set
    private var enchantments: MutableMap<Enchantment, Int>? = HashMap()

    /**
     * Returns the Displayname
     */
    var displayname: String? = null
        private set
    private var lore: MutableList<String>? = ArrayList()
    private var flags: MutableList<ItemFlag>? = ArrayList()

    /**
     * Returns if the '&' Character will be replaced
     */
    var andSymbol = true
        private set
    private var unsafeStackSize = false

    /**
     * Initalizes the ItemBuilder with [org.bukkit.Material]
     */
    constructor(material: Material?) {
        var material = material
        if (material == null) material = Material.AIR
        item = ItemStack(material)
        this.material = material
    }

    /**
     * Initalizes the ItemBuilder with [org.bukkit.Material] and Amount
     */
    constructor(material: Material?, amount: Int) {
        var material = material
        var amount = amount
        if (material == null) material = Material.AIR
        if (amount > material.maxStackSize || amount <= 0) amount = 1
        this.amount = amount
        item = ItemStack(material, amount)
        this.material = material
    }

    /**
     * Initalizes the ItemBuilder with [org.bukkit.Material], Amount and Displayname
     */
    constructor(material: Material?, amount: Int, displayname: String?) {
        var material = material
        var amount = amount
        if (material == null) material = Material.AIR
        Validate.notNull(displayname, "The Displayname is null.")
        item = ItemStack(material, amount)
        this.material = material
        if (amount > material.maxStackSize || amount <= 0 && !unsafeStackSize) amount = 1
        this.amount = amount
        this.displayname = displayname
    }

    /**
     * Initalizes the ItemBuilder with [org.bukkit.Material] and Displayname
     */
    constructor(material: Material?, displayname: String?) {
        var material = material
        if (material == null) material = Material.AIR
        Validate.notNull(displayname, "The Displayname is null.")
        item = ItemStack(material)
        this.material = material
        this.displayname = displayname
    }

    /**
     * Initalizes the ItemBuilder with a [org.bukkit.inventory.ItemStack]
     */
    constructor(item: ItemStack) {
        Validate.notNull(item, "The Item is null.")
        this.item = item
        if (item.hasItemMeta()) meta = item.itemMeta
        material = item.type
        amount = item.amount
        data = item.data
        durability = item.durability
        enchantments = item.enchantments
        if (item.hasItemMeta()) displayname = item.itemMeta!!.displayName
        if (item.hasItemMeta()) lore = item.itemMeta!!.lore
        if (item.hasItemMeta()) for (f in item.itemMeta!!.itemFlags) {
            flags!!.add(f)
        }
    }

    /**
     * Initalizes the ItemBuilder with a [org.bukkit.configuration.file.FileConfiguration] ItemStack in Path
     */
    constructor(cfg: FileConfiguration, path: String?) : this(cfg.getItemStack(path!!)!!)

    /**
     * Initalizes the ItemBuilder with an already existing [ItemBuilder]
     *
     */
    @Deprecated("Use the already initalized {@code ItemBuilder} Instance to improve performance")
    constructor(builder: ItemBuilder) {
        Validate.notNull(builder, "The ItemBuilder is null.")
        item = builder.item
        meta = builder.meta
        material = builder.material
        amount = builder.amount
        durability = builder.durability
        data = builder.data
        durability = builder.durability
        enchantments = builder.enchantments
        displayname = builder.displayname
        lore = builder.lore
        flags = builder.flags
    }

    /**
     * Sets the Amount of the ItemStack
     *
     * @param amount Amount for the ItemStack
     */
    fun amount(amount: Int): ItemBuilder {
        var amount = amount
        if (amount > material!!.maxStackSize || amount <= 0 && !unsafeStackSize) amount = 1
        this.amount = amount
        return this
    }

    /**
     * Sets the [org.bukkit.material.MaterialData] of the ItemStack
     *
     * @param data MaterialData for the ItemStack
     */
    fun data(data: MaterialData?): ItemBuilder {
        Validate.notNull(data, "The Data is null.")
        this.data = data
        return this
    }

    /**
     * Sets the Damage of the ItemStack
     *
     * @param damage Damage for the ItemStack
     */
    @Deprecated("Use {@code ItemBuilder#durability}")
    fun damage(damage: Short): ItemBuilder {
        durability = damage
        return this
    }

    /**
     * Sets the Durability (Damage) of the ItemStack
     *
     * @param damage Damage for the ItemStack
     */
    fun durability(damage: Short): ItemBuilder {
        durability = damage
        return this
    }

    /**
     * Sets the [org.bukkit.Material] of the ItemStack
     *
     * @param material Material for the ItemStack
     */
    fun material(material: Material?): ItemBuilder {
        Validate.notNull(material, "The Material is null.")
        this.material = material
        return this
    }

    /**
     * Sets the [org.bukkit.inventory.meta.ItemMeta] of the ItemStack
     *
     * @param meta Meta for the ItemStack
     */
    fun meta(meta: ItemMeta?): ItemBuilder {
        Validate.notNull(meta, "The Meta is null.")
        this.meta = meta
        return this
    }

    /**
     * Adds a [org.bukkit.enchantments.Enchantment] to the ItemStack
     *
     * @param enchant Enchantment for the ItemStack
     * @param level   Level of the Enchantment
     */
    fun enchant(enchant: Enchantment, level: Int): ItemBuilder {
        Validate.notNull(enchant, "The Enchantment is null.")
        enchantments!![enchant] = level
        return this
    }

    /**
     * Adds a list of [org.bukkit.enchantments.Enchantment] to the ItemStack
     *
     * @param enchantments Map containing Enchantment and Level for the ItemStack
     */
    fun enchant(enchantments: MutableMap<Enchantment, Int>?): ItemBuilder {
        Validate.notNull(enchantments, "The Enchantments are null.")
        this.enchantments = enchantments
        return this
    }

    /**
     * Sets the Displayname of the ItemStack
     *
     * @param displayname Displayname for the ItemStack
     */
    fun displayname(displayname: String?): ItemBuilder {
        Validate.notNull(displayname, "The Displayname is null.")
        this.displayname = if (andSymbol) ChatColor.translateAlternateColorCodes('&', displayname!!) else displayname
        return this
    }

    /**
     * Adds a Line to the Lore of the ItemStack
     *
     * @param line Line of the Lore for the ItemStack
     */
    fun lore(line: String?): ItemBuilder {
        Validate.notNull(line, "The Line is null.")
        lore!!.add((if (andSymbol) ChatColor.translateAlternateColorCodes('&', line!!) else line)!!)
        return this
    }

    /**
     * Sets the Lore of the ItemStack
     *
     * @param lore List containing String as Lines for the ItemStack Lore
     */
    fun lore(lore: MutableList<String>?): ItemBuilder {
        Validate.notNull(lore, "The Lores are null.")
        this.lore = lore
        return this
    }

    /**
     * Adds one or more Lines to the Lore of the ItemStack
     *
     * @param lines One or more Strings for the ItemStack Lore
     */
    @Deprecated("Use {@code ItemBuilder#lore}")
    fun lores(vararg lines: String?): ItemBuilder {
        Validate.notNull(lines, "The Lines are null.")
        for (line in lines) {
            lore(if (andSymbol) ChatColor.translateAlternateColorCodes('&', line!!) else line)
        }
        return this
    }

    /**
     * Adds one or more Lines to the Lore of the ItemStack
     *
     * @param lines One or more Strings for the ItemStack Lore
     */
    fun lore(vararg lines: String?): ItemBuilder {
        Validate.notNull(lines, "The Lines are null.")
        for (line in lines) {
            lore(if (andSymbol) ChatColor.translateAlternateColorCodes('&', line!!) else line)
        }
        return this
    }

    /**
     * Adds a String at a specified position in the Lore of the ItemStack
     *
     * @param line  Line of the Lore for the ItemStack
     * @param index Position in the Lore for the ItemStack
     */
    fun lore(line: String?, index: Int): ItemBuilder {
        Validate.notNull(line, "The Line is null.")
        lore!![index] = (if (andSymbol) ChatColor.translateAlternateColorCodes('&', line!!) else line)!!
        return this
    }

    /**
     * Adds a [org.bukkit.inventory.ItemFlag] to the ItemStack
     *
     * @param flag ItemFlag for the ItemStack
     */
    fun flag(flag: ItemFlag): ItemBuilder {
        Validate.notNull(flag, "The Flag is null.")
        flags!!.add(flag)
        return this
    }

    /**
     * Adds more than one [org.bukkit.inventory.ItemFlag] to the ItemStack
     *
     * @param flags List containing all ItemFlags
     */
    fun flag(flags: MutableList<ItemFlag>?): ItemBuilder {
        Validate.notNull(flags, "The Flags are null.")
        this.flags = flags
        return this
    }

    /**
     * Makes or removes the Unbreakable Flag from the ItemStack
     *
     * @param unbreakable If it should be unbreakable
     */
    fun unbreakable(unbreakable: Boolean): ItemBuilder {
        meta!!.isUnbreakable = unbreakable
        return this
    }

    /**
     * Makes the ItemStack Glow like it had a Enchantment
     */
    fun glow(): ItemBuilder {
        enchant(if (material != Material.BOW) Enchantment.ARROW_INFINITE else Enchantment.LUCK, 10)
        flag(ItemFlag.HIDE_ENCHANTS)
        return this
    }

    /**
     * Sets the Skin for the Skull
     *
     * @param user Username of the Skull
     */
    @Deprecated("Make it yourself - This Meta destrys the already setted Metas")
    fun owner(user: String?): ItemBuilder {
        Validate.notNull(user, "The Username is null.")
        if (material == Material.PLAYER_HEAD) {
            val smeta = meta as SkullMeta?
            smeta!!.owner = user
            meta = smeta
        }
        return this
    }

    fun texture(texture: String): ItemBuilder {
        if (material != Material.PLAYER_HEAD) return this
        val url = "http://textures.minecraft.net/texture/$texture"
        val profile = GameProfile(UUID.randomUUID(), null)
        val encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).toByteArray())
        profile.properties.put("textures", Property("textures", String(encodedData)))
        try {
            val skullMeta = item!!.itemMeta as SkullMeta? ?: throw NullPointerException("SkullMeta is Null")
            val profileField = skullMeta.javaClass.getDeclaredField("profile")
            profileField.isAccessible = true
            profileField[skullMeta] = profile
            item!!.itemMeta = skullMeta
        } catch (e1: NoSuchFieldException) {
            e1.printStackTrace()
        } catch (e1: IllegalArgumentException) {
            e1.printStackTrace()
        } catch (e1: IllegalAccessException) {
            e1.printStackTrace()
        }
        return this
    }

    /**
     * Returns the Unsafe Class containing NBT Methods
     */
    fun unsafe(): Unsafe {
        return Unsafe(this)
    }

    /**
     * Toggles replacement of the '&' Characters in Strings
     *
     */
    @Deprecated("Use {@code ItemBuilder#toggleReplaceAndSymbol}")
    fun replaceAndSymbol(): ItemBuilder {
        replaceAndSymbol(!andSymbol)
        return this
    }

    /**
     * Enables / Disables replacement of the '&' Character in Strings
     *
     * @param replace Determinates if it should be replaced or not
     */
    fun replaceAndSymbol(replace: Boolean): ItemBuilder {
        andSymbol = replace
        return this
    }

    /**
     * Toggles replacement of the '&' Character in Strings
     */
    fun toggleReplaceAndSymbol(): ItemBuilder {
        replaceAndSymbol(!andSymbol)
        return this
    }

    /**
     * Allows / Disallows Stack Sizes under 1 and above 64
     *
     * @param allow Determinates if it should be allowed or not
     */
    fun unsafeStackSize(allow: Boolean): ItemBuilder {
        unsafeStackSize = allow
        return this
    }

    /**
     * Toggles allowment of stack sizes under 1 and above 64
     */
    fun toggleUnsafeStackSize(): ItemBuilder {
        unsafeStackSize(!unsafeStackSize)
        return this
    }

    /**
     * Returns all Enchantments
     */
    fun getEnchantments(): Map<Enchantment, Int>? {
        return enchantments
    }

    /**
     * Returns the Lores
     */
    val lores: List<String>?
        get() = lore

    /**
     * Returns all ItemFlags
     */
    fun getFlags(): List<ItemFlag>? {
        return flags
    }

    /**
     * Returns all Lores
     *
     */
    @Deprecated("Use {@code ItemBuilder#getLores}")
    fun getLore(): List<String>? {
        return lore
    }

    /**
     * Converts the Item to a ConfigStack and writes it to path
     *
     * @param cfg  Configuration File to which it should be writed
     * @param path Path to which the ConfigStack should be writed
     */
    fun toConfig(cfg: FileConfiguration, path: String?): ItemBuilder {
        cfg[path!!] = build()
        return this
    }

    /**
     * Converts back the ConfigStack to a ItemBuilder
     *
     * @param cfg  Configuration File from which it should be read
     * @param path Path from which the ConfigStack should be read
     */
    fun fromConfig(cfg: FileConfiguration, path: String?): ItemBuilder {
        return ItemBuilder(cfg, path)
    }

    /**
     * Converts the ItemBuilder to a JsonItemBuilder
     *
     * @return The ItemBuilder as JSON String
     */
    fun toJson(): String {
        return Gson().toJson(this)
    }

    /**
     * Applies the currently ItemBuilder to the JSONItemBuilder
     *
     * @param json      Already existing JsonItemBuilder
     * @param overwrite Should the JsonItemBuilder used now
     */
    fun applyJson(json: String?, overwrite: Boolean): ItemBuilder {
        val b = Gson().fromJson(json, ItemBuilder::class.java)
        if (overwrite) return b
        if (b.displayname != null) displayname = b.displayname
        if (b.data != null) data = b.data
        if (b.material != null) material = b.material
        if (b.lore != null) lore = b.lore
        if (b.enchantments != null) enchantments = b.enchantments
        if (b.item != null) item = b.item
        if (b.flags != null) flags = b.flags
        durability = b.durability
        amount = b.amount
        return this
    }

    /**
     * Converts the ItemBuilder to a [org.bukkit.inventory.ItemStack]
     */
    fun build(): ItemStack? {
        item!!.type = material!!
        item!!.amount = amount
        item!!.durability = durability
        meta = item!!.itemMeta
        if (data != null) {
            item!!.data = data
        }
        if (enchantments!!.size > 0) {
            item!!.addUnsafeEnchantments(enchantments!!)
        }
        if (displayname != null) {
            meta!!.setDisplayName(displayname)
        }
        if (lore!!.size > 0) {
            meta!!.lore = lore
        }
        if (flags!!.size > 0) {
            for (f in flags!!) {
                meta!!.addItemFlags(f)
            }
        }
        item!!.itemMeta = meta
        return item
    }

    /**
     * Contains NBT Tags Methods
     */
    inner class Unsafe
    /**
     * Initalizes the Unsafe Class with a ItemBuilder
     */(
        /**
         * Do not access using this Field
         */
        protected val builder: ItemBuilder
    ) {
        /**
         * Do not access using this Field
         */
        protected val utils = ReflectionUtils()

        /**
         * Sets a NBT Tag `String` into the NBT Tag Compound of the Item
         *
         * @param key   The Name on which the NBT Tag should be saved
         * @param value The Value that should be saved
         */
        fun setString(key: String?, value: String?): Unsafe {
            builder.item = utils.setString(builder.item, key, value)
            return this
        }

        /**
         * Returns the String that is saved under the key
         */
        fun getString(key: String?): String? {
            return utils.getString(builder.item, key)
        }

        /**
         * Sets a NBT Tag `Integer` into the NBT Tag Compound of the Item
         *
         * @param key   The Name on which the NBT Tag should be savbed
         * @param value The Value that should be saved
         */
        fun setInt(key: String?, value: Int): Unsafe {
            builder.item = utils.setInt(builder.item, key, value)
            return this
        }

        /**
         * Returns the Integer that is saved under the key
         */
        fun getInt(key: String?): Int {
            return utils.getInt(builder.item, key)
        }

        /**
         * Sets a NBT Tag `Double` into the NBT Tag Compound of the Item
         *
         * @param key   The Name on which the NBT Tag should be savbed
         * @param value The Value that should be saved
         */
        fun setDouble(key: String?, value: Double): Unsafe {
            builder.item = utils.setDouble(builder.item, key, value)
            return this
        }

        /**
         * Returns the Double that is saved under the key
         */
        fun getDouble(key: String?): Double {
            return utils.getDouble(builder.item, key)
        }

        /**
         * Sets a NBT Tag `Boolean` into the NBT Tag Compound of the Item
         *
         * @param key   The Name on which the NBT Tag should be savbed
         * @param value The Value that should be saved
         */
        fun setBoolean(key: String?, value: Boolean): Unsafe {
            builder.item = utils.setBoolean(builder.item, key, value)
            return this
        }

        /**
         * Returns the Boolean that is saved under the key
         */
        fun getBoolean(key: String?): Boolean {
            return utils.getBoolean(builder.item, key)
        }

        /**
         * Returns a Boolean if the Item contains the NBT Tag named key
         */
        fun containsKey(key: String?): Boolean {
            return utils.hasKey(builder.item, key)
        }

        /**
         * Accesses back the ItemBuilder and exists the Unsafe Class
         */
        fun builder(): ItemBuilder {
            return builder
        }

        /**
         * This Class contains highly sensitive NMS Code that should not be touched unless you want to break the ItemBuilder
         */
        inner class ReflectionUtils {
            fun getString(item: ItemStack?, key: String?): String? {
                var compound = getNBTTagCompound(getItemAsNMSStack(item))
                if (compound == null) {
                    compound = newNBTTagCompound
                }
                try {
                    return compound!!.javaClass.getMethod("getString", String::class.java)
                        .invoke(compound, key) as String
                } catch (ex: IllegalAccessException) {
                    ex.printStackTrace()
                } catch (ex: InvocationTargetException) {
                    ex.printStackTrace()
                } catch (ex: NoSuchMethodException) {
                    ex.printStackTrace()
                }
                return null
            }

            fun setString(item: ItemStack?, key: String?, value: String?): ItemStack? {
                var nmsItem = getItemAsNMSStack(item)
                var compound = getNBTTagCompound(nmsItem)
                if (compound == null) {
                    compound = newNBTTagCompound
                }
                try {
                    compound!!.javaClass.getMethod("setString", String::class.java, String::class.java)
                        .invoke(compound, key, value)
                    nmsItem = setNBTTag(compound, nmsItem)
                } catch (ex: IllegalAccessException) {
                    ex.printStackTrace()
                } catch (ex: InvocationTargetException) {
                    ex.printStackTrace()
                } catch (ex: NoSuchMethodException) {
                    ex.printStackTrace()
                }
                return getItemAsBukkitStack(nmsItem)
            }

            fun getInt(item: ItemStack?, key: String?): Int {
                var compound = getNBTTagCompound(getItemAsNMSStack(item))
                if (compound == null) {
                    compound = newNBTTagCompound
                }
                try {
                    return compound!!.javaClass.getMethod("getInt", String::class.java).invoke(compound, key) as Int
                } catch (ex: IllegalAccessException) {
                    ex.printStackTrace()
                } catch (ex: InvocationTargetException) {
                    ex.printStackTrace()
                } catch (ex: NoSuchMethodException) {
                    ex.printStackTrace()
                }
                return -1
            }

            fun setInt(item: ItemStack?, key: String?, value: Int): ItemStack? {
                var nmsItem = getItemAsNMSStack(item)
                var compound = getNBTTagCompound(nmsItem)
                if (compound == null) {
                    compound = newNBTTagCompound
                }
                try {
                    compound!!.javaClass.getMethod("setInt", String::class.java, Int::class.java)
                        .invoke(compound, key, value)
                    nmsItem = setNBTTag(compound, nmsItem)
                } catch (ex: IllegalAccessException) {
                    ex.printStackTrace()
                } catch (ex: InvocationTargetException) {
                    ex.printStackTrace()
                } catch (ex: NoSuchMethodException) {
                    ex.printStackTrace()
                }
                return getItemAsBukkitStack(nmsItem)
            }

            fun getDouble(item: ItemStack?, key: String?): Double {
                var compound = getNBTTagCompound(getItemAsNMSStack(item))
                if (compound == null) {
                    compound = newNBTTagCompound
                }
                try {
                    return compound!!.javaClass.getMethod("getDouble", String::class.java)
                        .invoke(compound, key) as Double
                } catch (ex: IllegalAccessException) {
                    ex.printStackTrace()
                } catch (ex: InvocationTargetException) {
                    ex.printStackTrace()
                } catch (ex: NoSuchMethodException) {
                    ex.printStackTrace()
                }
                return Double.NaN
            }

            fun setDouble(item: ItemStack?, key: String?, value: Double): ItemStack? {
                var nmsItem = getItemAsNMSStack(item)
                var compound = getNBTTagCompound(nmsItem)
                if (compound == null) {
                    compound = newNBTTagCompound
                }
                try {
                    compound!!.javaClass.getMethod("setDouble", String::class.java, Double::class.java)
                        .invoke(compound, key, value)
                    nmsItem = setNBTTag(compound, nmsItem)
                } catch (ex: IllegalAccessException) {
                    ex.printStackTrace()
                } catch (ex: InvocationTargetException) {
                    ex.printStackTrace()
                } catch (ex: NoSuchMethodException) {
                    ex.printStackTrace()
                }
                return getItemAsBukkitStack(nmsItem)
            }

            fun getBoolean(item: ItemStack?, key: String?): Boolean {
                var compound = getNBTTagCompound(getItemAsNMSStack(item))
                if (compound == null) {
                    compound = newNBTTagCompound
                }
                try {
                    return compound!!.javaClass.getMethod("getBoolean", String::class.java)
                        .invoke(compound, key) as Boolean
                } catch (ex: IllegalAccessException) {
                    ex.printStackTrace()
                } catch (ex: InvocationTargetException) {
                    ex.printStackTrace()
                } catch (ex: NoSuchMethodException) {
                    ex.printStackTrace()
                }
                return false
            }

            fun setBoolean(item: ItemStack?, key: String?, value: Boolean): ItemStack? {
                var nmsItem = getItemAsNMSStack(item)
                var compound = getNBTTagCompound(nmsItem)
                if (compound == null) {
                    compound = newNBTTagCompound
                }
                try {
                    compound!!.javaClass.getMethod("setBoolean", String::class.java, Boolean::class.java)
                        .invoke(compound, key, value)
                    nmsItem = setNBTTag(compound, nmsItem)
                } catch (ex: IllegalAccessException) {
                    ex.printStackTrace()
                } catch (ex: InvocationTargetException) {
                    ex.printStackTrace()
                } catch (ex: NoSuchMethodException) {
                    ex.printStackTrace()
                }
                return getItemAsBukkitStack(nmsItem)
            }

            fun hasKey(item: ItemStack?, key: String?): Boolean {
                var compound = getNBTTagCompound(getItemAsNMSStack(item))
                if (compound == null) {
                    compound = newNBTTagCompound
                }
                try {
                    return compound!!.javaClass.getMethod("hasKey", String::class.java).invoke(compound, key) as Boolean
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                }
                return false
            }

            val newNBTTagCompound: Any?
                get() {
                    val ver = Bukkit.getServer().javaClass.getPackage().name.split(".".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()[3]
                    try {
                        return Class.forName("net.minecraft.server.$ver.NBTTagCompound").newInstance()
                    } catch (ex: ClassNotFoundException) {
                        ex.printStackTrace()
                    } catch (ex: IllegalAccessException) {
                        ex.printStackTrace()
                    } catch (ex: InstantiationException) {
                        ex.printStackTrace()
                    }
                    return null
                }

            fun setNBTTag(tag: Any?, item: Any?): Any? {
                try {
                    item!!.javaClass.getMethod("setTag", item.javaClass).invoke(item, tag)
                    return item
                } catch (ex: IllegalAccessException) {
                    ex.printStackTrace()
                } catch (ex: InvocationTargetException) {
                    ex.printStackTrace()
                } catch (ex: NoSuchMethodException) {
                    ex.printStackTrace()
                }
                return null
            }

            fun getNBTTagCompound(nmsStack: Any?): Any? {
                try {
                    return nmsStack!!.javaClass.getMethod("getTag").invoke(nmsStack)
                } catch (ex: IllegalAccessException) {
                    ex.printStackTrace()
                } catch (ex: InvocationTargetException) {
                    ex.printStackTrace()
                } catch (ex: NoSuchMethodException) {
                    ex.printStackTrace()
                }
                return null
            }

            fun getItemAsNMSStack(item: ItemStack?): Any? {
                try {
                    val m = craftItemStackClass!!.getMethod("asNMSCopy", ItemStack::class.java)
                    return m.invoke(craftItemStackClass, item)
                } catch (ex: NoSuchMethodException) {
                    ex.printStackTrace()
                } catch (ex: IllegalAccessException) {
                    ex.printStackTrace()
                } catch (ex: InvocationTargetException) {
                    ex.printStackTrace()
                }
                return null
            }

            fun getItemAsBukkitStack(nmsStack: Any?): ItemStack? {
                try {
                    val m = craftItemStackClass!!.getMethod("asCraftMirror", nmsStack!!.javaClass)
                    return m.invoke(craftItemStackClass, nmsStack) as ItemStack
                } catch (ex: NoSuchMethodException) {
                    ex.printStackTrace()
                } catch (ex: InvocationTargetException) {
                    ex.printStackTrace()
                } catch (ex: IllegalAccessException) {
                    ex.printStackTrace()
                }
                return null
            }

            val craftItemStackClass: Class<*>?
                get() {
                    val ver = Bukkit.getServer().javaClass.getPackage().name.split(".".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()[3]
                    try {
                        return Class.forName("org.bukkit.craftbukkit.$ver.inventory.CraftItemStack")
                    } catch (ex: ClassNotFoundException) {
                        ex.printStackTrace()
                    }
                    return null
                }
        }
    }

    companion object {
        /**
         * Converts the Item to a ConfigStack and writes it to path
         *
         * @param cfg     Configuration File to which it should be writed
         * @param path    Path to which the ConfigStack should be writed
         * @param builder Which ItemBuilder should be writed
         */
        fun toConfig(cfg: FileConfiguration, path: String?, builder: ItemBuilder) {
            cfg[path!!] = builder.build()
        }

        /**
         * Converts the ItemBuilder to a JsonItemBuilder
         *
         * @param builder Which ItemBuilder should be converted
         * @return The ItemBuilder as JSON String
         */
        fun toJson(builder: ItemBuilder?): String {
            return Gson().toJson(builder)
        }

        /**
         * Converts the JsonItemBuilder back to a ItemBuilder
         *
         * @param json Which JsonItemBuilder should be converted
         */
        fun fromJson(json: String?): ItemBuilder {
            return Gson().fromJson(json, ItemBuilder::class.java)
        }
    }
}
