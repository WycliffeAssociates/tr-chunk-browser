package com.matthewrussell.trchunkbrowser.domain

import com.jfoenix.controls.JFXButton
import com.matthewrussell.trchunkbrowser.model.Language
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import java.text.MessageFormat
import java.util.*
import java.util.Locale
import java.util.concurrent.Callable


/**
 * I18N utility class..
 *
 */
object I18N {
    /** the current selected Locale.  */
    private var locale: ObjectProperty<Locale>? = null

    val availableLanguages = listOf(
        Language("en", "English"),
        Language("ru", "Русский")
    )

    /**
     * get the default locale.
     *
     * @return
     */
    private val defaultLocale: Locale
        get() {
            return Locale.ENGLISH
        }

    fun getLocale(): Locale {
        return locale!!.get()
    }

    fun setLocale(locale: Locale) {
        localeProperty()!!.set(locale)
        Locale.setDefault(locale)
    }

    private fun localeProperty(): ObjectProperty<Locale>? {
        return locale
    }

    /**
     * gets the string with the given key from the resource bundle for the current locale and uses it as first argument
     * to MessageFormat.format, passing in the optional args and returning the result.
     *
     * @param key
     * message key
     * @param args
     * optional arguments for the message
     * @return localized formatted string
     */
    fun get(key: String, vararg args: Any?): String {
        val bundle = ResourceBundle.getBundle("Messages", getLocale())
        return MessageFormat.format(bundle.getString(key), *args)
    }

    /**
     * creates a String binding to a localized String for the given message bundle key
     *
     * @param key
     * key
     * @return String binding
     */
    private fun createStringBinding(key: String, vararg args: Any?): StringBinding {
        return Bindings.createStringBinding(
            Callable { get(key, *args) },
            locale
        )
    }

    /**
     * creates a String Binding to a localized String that is computed by calling the given func
     *
     * @param func
     * function called on every change
     * @return StringBinding
     */
    private fun createStringBinding(func: Callable<String>): StringBinding {
        return Bindings.createStringBinding(func, locale)
    }

    /**
     * creates a bound Label whose value is computed on language change.
     *
     * @param key
     * ResourceBundle key
     * @param args
     * optional arguments for the message
     * @return Label
     */
    fun label(key: String, vararg args: Any?): Label {
        val label = Label()
        label.textProperty().bind(createStringBinding(key, args))
        return label
    }

    /**
     * creates a bound Label whose value is computed on language change.
     *
     * @param func
     * the function to compute the value
     * @return Label
     */
    fun label(func: Callable<String>): Label {
        val label = Label()
        label.textProperty().bind(createStringBinding(func))
        return label
    }

    /**
     * creates a bound Button for the given resourcebundle key
     *
     * @param key
     * ResourceBundle key
     * @param args
     * optional arguments for the message
     * @return Button
     */
    fun button(key: String, vararg args: Any?): Button {
        val button = Button()
        button.textProperty().bind(createStringBinding(key, *args))
        return button
    }

    /**
     * creates a bound Button for the given callable
     *
     * @param func
     * Callable function
     * @return Button
     */
    fun button(func: Callable<String>): Button {
        val button = Button()
        button.textProperty().bind(createStringBinding(func))
        return button
    }

    /**
     * creates a bound JFXButton for the given resourcebundle key
     *
     * @param func
     * ResourceBundle key
     * @param args
     * optional arguments for the message
     * @return Button
     */
    fun jFXButton(key: String, vararg args: Any?): JFXButton {
        val button = JFXButton()
        button.textProperty().bind(createStringBinding(key, *args))
        return button
    }

    /**
     * creates a bound JFXButton for the given callable
     *
     * @param key
     * function called on every change
     * @return Button
     */
    fun jFXButton(func: Callable<String>): JFXButton {
        val button = JFXButton()
        button.textProperty().bind(createStringBinding(func))
        return button
    }

    /**
     * creates a bound Tooltip for the given resourcebundle key
     *
     * @param key
     * ResourceBundle key
     * @param args
     * optional arguments for the message
     * @return Label
     */
    fun tooltip(key: String, vararg args: Any?): Tooltip {
        val tooltip = Tooltip()
        tooltip.textProperty().bind(createStringBinding(key, *args))
        return tooltip
    }

    /**
     * creates a bound Tooltip for the given callable
     *
     * @param func
     * Callable function
     * @return Label
     */
    fun tooltip(func: Callable<String>): Tooltip {
        val tooltip = Tooltip()
        tooltip.textProperty().bind(createStringBinding(func))
        return tooltip
    }

    init {
        locale = SimpleObjectProperty(defaultLocale)
        locale?.addListener { _: ObservableValue<out Locale>?, _: Locale?, newValue: Locale? ->
                Locale.setDefault(newValue)
            }
    }
}