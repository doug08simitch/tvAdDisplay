# 📺 BroadcastOverlay — Java TV Advertising System

A lightweight Java application that turns any Windows PC into a
professional broadcast advertising display — just like SuperSport,
ESPN, or BBC Sport. Runs as a **transparent always-on-top overlay**
so your TV or monitor shows whatever it normally shows, with a
scrolling ticker bar at the bottom and animated ad panels sliding
in from the side.

No special hardware needed. Plug any PC into a TV via HDMI and run.

---

## Preview

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│        [ Your TV content / any application underneath ]         │
│                                                                 │
│                                          ┌─────────────────┐   │
│                                          │  YOUR BRAND     │   │
│                                          │  Your Headline  │   │
│                                          │  Your tagline   │   │
│                                          └─────────────────┘   │
├──────────┬──────────────────────────────────────┬──────────────┤
│  LIVE  ● │  YOUR SCROLLING MESSAGE HERE  ◆  ... │  MON 18 JAN  │
│          │  CONTINUOUS TICKER TEXT SCROLLS ...  │   21:45:30   │
└──────────┴──────────────────────────────────────┴──────────────┘
```

---

## Features

- 📰 **Scrolling ticker bar** — continuous red bar permanently at the bottom of the screen
- 🕐 **Live clock and date** — always visible on the right side of the ticker
- 🔴 **LIVE badge** — pulsing red dot on the left of the ticker
- 📢 **Side ad panels** — slides in smoothly from the right on a schedule, then slides back out
- ✨ **Animated border** — pulsing gold glow effect on ad panels
- 🖼️ **Image support** — display any product or brand image inside ad panels
- 💾 **USB flash drive support** — plug in a flash drive and content loads automatically
- 🔄 **Auto content reload** — content updates within 5 seconds without restarting
- 📝 **Auto config creation** — creates a ready-to-edit config file on first run
- 🔁 **Fallback content** — shows default content if no config file is found
- 🖥️ **Works on any screen** — laptop, desktop monitor, or TV via HDMI

---

## Use Cases

- 🍺 Bars and restaurants — display drink offers and promotions
- 🏪 Retail shops — show product prices and deals
- 🏨 Hotels and lodges — display services and announcements
- 🏋️ Gyms — show class schedules and membership offers
- 🏫 Schools and colleges — display notices and announcements
- 🏥 Clinics and hospitals — show health tips and queue information
- 🎪 Events — display sponsor ads and event schedules
- 🏢 Offices — show internal announcements and company news
- ⛽ Petrol stations — display fuel prices and promotions

---

## Requirements

| Requirement | Details |
|---|---|
| Java JDK | Version 11 or newer |
| Operating System | Windows 10 / Windows 11 |
| Display | Any monitor or TV via HDMI |
| RAM | Minimum 256MB free |
| Disk space | Less than 1MB |

Download Java JDK free from: **https://adoptium.net**

---

## Project Structure

```
BroadcastOverlay/
│
├── ParadiseTV.java       # Main source file — rename to match your class name
└── README.md             # This file

Desktop/
└── ParadiseTV.txt        # Content config file — auto-created on first run

Flash Drive (optional)/
└── ParadiseTV.txt        # USB config — takes priority over Desktop file
```

---

## Getting Started

### Step 1 — Install Java JDK
Download and install from **https://adoptium.net**
After installing open Command Prompt and verify:
```
java -version
javac -version
```
Both should print a version number.

### Step 2 — Open in IntelliJ IDEA
- Open IntelliJ IDEA
- Create a new Java project
- Copy `ParadiseTV.java` into the `src` folder
- Click the green **Run** button

### Step 3 — First Run
On first run the program automatically:
- Creates `ParadiseTV.txt` on your Desktop with example content
- Starts the ticker bar at the bottom of your screen
- Schedules the first ad to appear after 30 seconds

### Step 4 — Customize Your Content
Open `ParadiseTV.txt` on your Desktop with Notepad and edit freely.
Content reloads automatically within 5 seconds — no restart needed.

---

## Configuration File

The file `ParadiseTV.txt` controls everything the program displays.
It uses a simple format that anyone can edit in Notepad.

**File is read from flash drive first, then Desktop as fallback.**

### Full Example

```ini
# BroadcastOverlay Configuration File
# Edit this file to change what shows on screen
# Lines starting with # are comments and are ignored
# Changes reload automatically — no restart needed

[ticker]
WELCOME TO OUR STORE — THE BEST DEALS IN TOWN
SPECIAL OFFER: PRODUCT A — ONLY $9.99 TODAY
PRODUCT B NOW AVAILABLE — LIMITED STOCK
FOLLOW US ON SOCIAL MEDIA FOR MORE OFFERS
OPEN MONDAY TO SUNDAY 8AM TO 10PM

[ads]
# Format: Brand | Headline | Tagline | C:\path\to\image.jpg
# Leave image path empty if you have no image
Nike | Just Do It | New season collection available now | C:\ads\nike.jpg
Coca-Cola | Taste the Feeling | Refreshing every moment | C:\ads\coke.jpg
Your Brand | Your Headline | Your tagline here |
```

### Sections Explained

#### `[ticker]` — Scrolling messages
Each line becomes one message in the scrolling ticker bar.
Add as many lines as you want.
```
[ticker]
YOUR FIRST MESSAGE
YOUR SECOND MESSAGE
YOUR THIRD MESSAGE
```

#### `[ads]` — Side advertisement panels
Each line is one advertisement that slides in from the side.
```
[ads]
Brand | Headline | Tagline | C:\full\path\to\image.jpg
```

| Field | Required | Description |
|---|---|---|
| Brand | Yes | Shown in gold at the top of the ad panel |
| Headline | Yes | Main message in white bold text |
| Tagline | Yes | Supporting text in light blue |
| Image path | No | Full path to image file — leave empty if none |

Supported image formats: `.jpg` `.png` `.gif` `.bmp`

---

## Flash Drive Setup

Perfect for businesses that want to update content easily
without touching the PC — just edit the file on the flash drive.

### Setup
1. Copy `ParadiseTV.txt` to the **root of your flash drive**
   (not inside any folder — directly on the drive)
2. Plug the flash drive into the PC
3. Program automatically detects it within 5 seconds
4. Content from flash drive loads and replaces current content

```
Flash Drive/
├── ParadiseTV.txt          ← must be here at the root
├── product_image.jpg       ← images can also be on flash drive
└── brand_logo.png
```

For images stored on the flash drive use the drive letter:
```
[ads]
Your Brand | Special Offer | Limited time only | D:\product_image.jpg
```

### What happens when flash drive is removed
- Program detects the removal within 5 seconds
- Automatically falls back to `ParadiseTV.txt` on the Desktop
- If Desktop file also missing — shows built-in default content
- No crashes, no restarts needed

---

## TV / Display Deployment

### Option 1 — Laptop connected to TV
```
Laptop running ParadiseTV ──► HDMI cable ──► TV
```
Simplest setup. Just connect and run.

### Option 2 — Windows Mini PC (recommended for permanent install)
```
Flash Drive ──► Mini PC ──► HDMI cable ──► TV
```

**Recommended Mini PC specs:**
- Windows 10 or 11
- Any modern Intel or AMD processor
- 4GB RAM
- 2 USB ports minimum
- HDMI output
- Price: $50 – $150 / Ksh 6,000 – 18,000

**Setup steps:**
1. Install Java JDK on the Mini PC
2. Copy `ParadiseTV.java` to the Mini PC
3. Open Command Prompt and compile:
   ```
   javac ParadiseTV.java
   ```
4. Connect Mini PC to TV via HDMI
5. Plug in flash drive with `ParadiseTV.txt`
6. Run the program:
   ```
   java ParadiseTV
   ```

### Auto-Start on Boot
To make the program start automatically every time the PC turns on:

1. Press `Win + R` and type `shell:startup` then press Enter
2. Create a new file called `start_broadcast.bat`
3. Paste this inside and save:
   ```bat
   @echo off
   cd C:\path\to\your\project
   java ParadiseTV
   ```
4. Done — program launches automatically on every boot

---

## How It Works

```
Program starts
      │
      ▼
Scan USB drives (D: E: F: G: H: I: J:) for ParadiseTV.txt
      │
      ├── Found on USB ──────────────────► load from USB
      │
      └── Not found on USB
                │
                ▼
           Check Desktop for ParadiseTV.txt
                │
                ├── Found ────────────────► load from Desktop
                │
                └── Not found
                          │
                          ▼
                     Create default file + load built-in content
                          │
                          ▼
                   Start ticker bar at bottom of screen
                   Start ad scheduler
                   Start file watcher (checks every 5 seconds)
                          │
                          ▼
                   Running until manually stopped
```

---

## Timing Reference

| Event | Default Timing |
|---|---|
| First ad appears | 30 seconds after launch |
| Ad interval | Every 5 minutes |
| Ad visible on screen | 12 seconds |
| Content reload check | Every 5 seconds |
| Clock update | Every 1 second |

---

## Customization Guide

### Change ticker speed
Find this line in `ParadiseTV.java`:
```java
scrollX -= 1.5f;
```
- Increase the number for faster scrolling
- Decrease for slower scrolling
- `1.0f` = slow, `1.5f` = normal, `3.0f` = fast

### Change ad interval
Find this line in `ParadiseAdManager`:
```java
private static final long INTERVAL_SECS = 5 * 60;
```
Change `5 * 60` to any number of seconds you want.
Example: `2 * 60` = every 2 minutes

### Change how long ads stay visible
Find this line:
```java
private static final long DISPLAY_MS = 12_000;
```
Change `12_000` to milliseconds.
Example: `20_000` = 20 seconds

### Change ticker bar color
Find this in `paintComponent` of `ParadiseTickerPanel`:
```java
g2.setPaint(new GradientPaint(
    0, 0, new Color(200, 18, 18),   // top color (RGB)
    0, H, new Color(130,  8,  8))); // bottom color (RGB)
```
Change the RGB values to any color you want.
- Red: `200, 18, 18`
- Blue: `18, 60, 200`
- Green: `18, 150, 50`
- Black: `20, 20, 20`
- Purple: `100, 18, 180`

---

## Controls

| Action | How |
|---|---|
| Stop the program | Click red Stop button in IntelliJ |
| Update ticker messages | Edit `[ticker]` section in `ParadiseTV.txt` — reloads in 5 seconds |
| Update ads | Edit `[ads]` section in `ParadiseTV.txt` — reloads in 5 seconds |
| Change content source | Plug in or remove flash drive — switches in 5 seconds |

---

## Troubleshooting

**Ticker not showing**
- Make sure Java is installed: run `java -version` in Command Prompt
- Check IntelliJ Run tab for any error messages

**Content not updating**
- Make sure you saved the file with `Ctrl + S` in Notepad
- Check that the file is named exactly `ParadiseTV.txt` not `ParadiseTV.txt.txt`
- In File Explorer click View → tick File name extensions to check

**Flash drive not detected**
- Make sure `ParadiseTV.txt` is at the root of the drive not inside a folder
- Flash drive letter must be between D and J
- Wait up to 5 seconds after plugging in

**Image not showing in ad**
- Use the full path: `C:\Users\YourName\Pictures\image.jpg`
- Make sure the image file actually exists at that path
- Supported formats: jpg, png, gif, bmp only

**Program crashes on start**
- Check the IntelliJ Run tab for the error message
- Most common cause: Color value out of range — all RGB and alpha values must be 0–255

---

## Built With

- **Java 11+** — core language
- **Java Swing** — overlay window system
- **Java AWT** — 2D graphics and animation
- **javax.imageio** — image loading
- **java.util.concurrent** — ad scheduling
- **java.io** — file reading and writing

---

## Contributing

Pull requests are welcome. For major changes please open an issue
first to discuss what you would like to change.

---

## License

MIT License — free to use, modify, and distribute for any purpose.

```
MIT License

Copyright (c) 2025

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software to deal in the Software without restriction,
including the rights to use, copy, modify, merge, publish, distribute,
and sublicense.
```
