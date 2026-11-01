# Kodopilot for JetBrains IDEs

![Build](https://github.com/RuslanBystr/kodopilot-jetbrains/actions/workflows/build.yml/badge.svg)
<!-- Uncomment after the first Marketplace release and replace MARKETPLACE_ID:
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
-->

<!-- Plugin description -->
**Kodopilot** is an AI coding assistant for WebStorm and other JetBrains IDEs.

TODO: one or two sentences on what the plugin does and the problem it solves.

**Features**
- TODO: feature 1
- TODO: feature 2
- TODO: feature 3
<!-- Plugin description end -->

## Installation

**From JetBrains Marketplace** *(after the first release)*

<kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > search for <kbd>Kodopilot</kbd> > <kbd>Install</kbd>

**Manually**

Download the [latest release](https://github.com/RuslanBystr/kodopilot-jetbrains/releases/latest) and install it via
<kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install Plugin from Disk...</kbd>

## Usage

TODO: how to open the plugin, main actions and shortcuts, a screenshot or GIF.

## Configuration

TODO: settings (API key, model, etc.) and where to find them in <kbd>Settings</kbd>.

## Development

Requirements: JDK 21, IntelliJ IDEA (Community or Ultimate).

```bash
# Run an IDE sandbox with the plugin installed
./gradlew runIde

# Build the plugin ZIP (output: build/distributions/)
./gradlew buildPlugin

# Run tests and plugin verification
./gradlew check verifyPlugin
```

## Compatibility

TODO: supported IDE versions (see `pluginSinceBuild` in `gradle.properties`).

## License

TODO: license (e.g. MIT).

---
Built with the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template).
