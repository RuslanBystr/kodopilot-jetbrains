package com.github.ruslanbystr.kodopilotplugin.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.JBColor
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.GradientPaint
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.border.AbstractBorder

class PromptPopup(
    private val project: Project,
    private val fileName: String
) {

    private val aiIcon = IconLoader.getIcon(
        "/icons/ai-sparkle-13x13.svg",
        PromptPopup::class.java
    )

    private val textColor = JBColor(
        Color(0xFFFFFF),
        Color(0xFFFFFF)
    )

    fun show(onSubmit: (String) -> Unit) {
        val textField = JBTextField().apply {
            emptyText.text = "Ask KodoPilot..."

            preferredSize = JBUI.size(650, 42)
            minimumSize = JBUI.size(650, 42)

            border = RoundedBorder()
            margin = JBInsets(0, 14, 0, 14)
        }

        textField.foreground = UIUtil.getLabelForeground()
        textField.caretColor = textColor
        textField.font = textField.font.deriveFont(16f)

        val title = JBLabel("Describe what you'd like to change...", aiIcon, JBLabel.LEADING).apply {
            font = font.deriveFont(Font.BOLD, 15f)
        }

        val file = JBLabel("Editing: $fileName").apply {
            foreground = JBUI.CurrentTheme.ContextHelp.FOREGROUND
        }

        val header = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false

            add(title)
            add(Box.createVerticalStrut(6))
            add(file)
        }

        val panel = JPanel(BorderLayout(JBUI.scale(8), JBUI.scale(10))).apply {
            border = JBUI.Borders.empty(12)
            isOpaque = true

            add(header, BorderLayout.NORTH)
            add(textField, BorderLayout.CENTER)
        }

        val popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(panel, textField)
            .setRequestFocus(true)
            .setFocusable(true)
            .setMovable(false)
            .setResizable(false)
            .setCancelOnClickOutside(true)
            .setShowBorder(false)
            .createPopup()

        textField.addActionListener {
            val prompt = textField.text.trim()

            if (prompt.isNotBlank()) {
                popup.cancel()
                onSubmit(prompt)
            }
        }

        popup.showCenteredInCurrentWindow(project)
    }
}

private class RoundedBorder : AbstractBorder() {
    private val borderColor = JBColor(
        Color(0x5EE8D0),
        Color(0x5EE8D0)
    )

    override fun paintBorder(
        c: java.awt.Component,
        g: Graphics,
        x: Int,
        y: Int,
        width: Int,
        height: Int
    ) {
        val g2 = g as Graphics2D

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        )

        g2.color = borderColor

        val paint = GradientPaint(
            0f,
            0f,
            Color(0x5EE8D0),
            width.toFloat(),
            0f,
            Color(0x1A8FA8)
        )

        g2.paint = paint

        g2.drawRoundRect(
            x,
            y,
            width - 1,
            height - 1,
            18,
            18
        )
    }

    override fun getBorderInsets(c: java.awt.Component): JBInsets =
        JBInsets(10, 14, 10, 14)

    override fun getBorderInsets(
        c: java.awt.Component,
        insets: java.awt.Insets
    ): java.awt.Insets {
        insets.set(10, 14, 10, 14)
        return insets
    }
}
