# ParadiseTV 📺

A Java-based broadcast overlay system for **Paradise Club and Restaurant**.
Runs as a transparent always-on-top overlay on any Windows PC or Mini PC
connected to a TV via HDMI — just like mainstream broadcast TV (SuperSport,
ESPN style).

---

## Preview

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│   [Your TV content / any application runs here normally]        │
│                                                                 │
│                                              ┌───────────────┐  │
│                                              │  PARADISE TV  │  │
│                                              │  Kenya Cane   │  │
│                                              │ Enjoy KC Pine │  │
│                                              │ Smooth taste  │  │
│                                              └───────────────┘  │
├──────────┬──────────────────────────────────────────┬──────────┤
│   LIVE ● │  WELCOME TO PARADISE CLUB AND RESTAURANT │ MON 18   │
│          │  WHERE THE FUN NEVER ENDS  ◆  KENYA ...  │ 21:45:30 │
└──────────┴──────────────────────────────────────────┴──────────┘
```

---

## Features

- **Transparent overlay** — sits on top of everything, TV content shows underneath
- **Continuous ticker bar** — red scrolling bar permanently at the bottom of screen
- **Live clock and date** — always visible on the right side of the ticker
- **LIVE badge** — pulsing red dot on the left side of the ticker
- **Side ad panels** — slides in smoothly from the right every 5 minutes, stays 12 seconds then slides back out
- **Animated gold border** — pulsing glow effect on ad panels
- **Image support** — display product images inside ad panels
- **Flash drive support** — plug in a USB drive and content loads automatically
- **Auto file watcher** — content reloads every 5 seconds without restarting
- **Auto config creation** — creates a default config file on first run
- **Fallback content** — shows default Paradise Club content if no config file found

---

## Requirements

| Requirement | Details |
|---|---|
| Java JDK | Version 11 or newer |
| Operating System | Windows 10 / Windows 11 |
| Display | Any screen or TV via HDMI |
| RAM | Minimum 256MB free |

Download Java JDK free from: **https://adoptium.net**

---

## Project Structure

```
ParadiseTV/
│
├── ParadiseTV.java          # Main source file — all code in one file
└── README.md                # This file

Desktop/
└── ParadiseTV.txt           # Content config file (auto-created on first run)

Flash Drive (optional)/
└── ParadiseTV.txt           # Config file on USB — overrides Desktop file
```

---

## Getting Started

### 1. Install Java JDK
Download and install from **https://adoptium.net**  
After installing, verify by opening Command Prompt and typing:
```
java -version
javac -version
```

### 2. Open in IntelliJ IDEA
- Open IntelliJ IDEA
- Create a new Java project
- Copy `ParadiseTV.java` into the `src` folder
- Click the green **Run** button

### 3. First Run
On first run the program will:
- Automatically create `ParadiseTV.txt` on your Desktop
- Load default Paradise Club content
- Show the ticker bar at the bottom of your screen
- Schedule ads to appear after 30 seconds

### 4. Customize Content
Open `ParadiseTV.txt` on your Desktop with Notepad and edit:
```
[ticker]
YOUR MESSAGE HERE
ANOTHER MESSAGE HERE

[ads]
Brand Name | Headline | Tagline | C:\path\to\image.jpg
```
Content reloads automatically within 5 seconds — no restart needed.

---

## Configuration File

The file `ParadiseTV.txt` controls everything displayed.  
It is read from the **flash drive first**, then falls back to the **Desktop**.

### Full Example

```ini
# ParadiseTV Configuration File
# Edit this file to change what shows on screen
# Changes reload automatically within 5 seconds

[ticker]
WELCOME TO PARADISE CLUB AND RESTAURANT WHERE THE FUN NEVER ENDS
EASTER SPECIAL OFFER: KENYA CANE 250ML - KSH 320
EASTER SPECIAL OFFER: KENYA CANE 750ML - KSH 900
DALLAS 250ML - KSH 160
MR MICHAEL CHETAMBE WISHES YOU HAPPY EASTER HOLIDAYS
DON'T MISS OUT EXCLUSIVE ENGLISH PREMIER LEAGUE LIVE
MPESA TILL NO: 5676214 COUNTER B DURING THE DAY
MPESA TILL NO: 5676212 COUNTER A DURING THE NIGHT
IN CASE OF ISSUES REPORT TO MANAGEMENT IMMEDIATELY

[ads]
# Format: Brand | Headline | Tagline | imagepath (optional)
Kenya Cane | Enjoy KC Pineapple Fusion | Refreshingly smooth every sip | C:\Users\user\Desktop\kc.jpg
Kenya Cane | Enjoy KC Lemon and Ginger | The perfect blend of flavour |
Paradise Club | Easter Special Offers | Great drinks at great prices today |
```

### Sections Explained

| Section | Purpose |
|---|---|
| `[ticker]` | Each line is one scrolling message at the bottom |
| `[ads]` | Each line is one advertisement that slides in from the side |

### Ad Format
```
Brand | Headline | Tagline | C:\full\path\to\image.jpg
```
- **Brand** — shown in gold at the top of the ad panel
- **Headline** — shown in white bold text
- **Tagline** — shown in light blue smaller text
- **Image path** — full path to JPG or PNG file (leave empty if no image)

Supported image formats: `.jpg` `.png` `.gif` `.bmp`

---

## Flash Drive Setup

1. Copy `ParadiseTV.txt` to the **root of your flash drive** (not inside any folder)
2. Plug the flash drive into the PC
3. The program scans drive letters D through J automatically
4. When found it loads content from the flash drive
5. If flash drive is removed it falls back to Desktop content within 5 seconds

```
Flash Drive
└── ParadiseTV.txt        ← must be here, not in a subfolder
└── kc_pineapple.jpg      ← images can also be on the flash drive
└── kc_lemon.jpg
```

For images on the flash drive use the drive letter path:
```
Kenya Cane | Enjoy KC Pineapple | Smooth every sip | D:\kc_pineapple.jpg
```

---

## Deployment on TV (Windows Mini PC + HDMI)

```
Flash Drive ──► Windows Mini PC ──► HDMI Cable ──► Smart TV
                     │
                 ParadiseTV
                  running
```

### Recommended Mini PC Specs
- Windows 10 or 11
- Intel or AMD processor (any modern one)
- 4GB RAM minimum
- 2 USB ports (one for flash drive, one for keyboard/mouse during setup)
- HDMI output port
- Price range: Ksh 8,000 – 15,000

### Setup Steps on Mini PC
1. Install Java JDK on the Mini PC
2. Copy `ParadiseTV.java` to the Mini PC
3. Compile once: open Command Prompt and run `javac ParadiseTV.java`
4. Connect Mini PC to TV via HDMI
5. Plug in flash drive with `ParadiseTV.txt`
6. Run: `java ParadiseTV`
7. Program runs in background — TV shows overlay continuously

### Auto-Start on Boot (Optional)
To make ParadiseTV start automatically when the Mini PC turns on:
1. Press `Win + R` and type `shell:startup`
2. Create a file called `start_paradise.bat` with this content:
```bat
@echo off
cd C:\path\to\ParadiseTV
java ParadiseTV
```
3. Save it in the startup folder — program will launch on every boot

---

## How It Works

```
Program starts
     │
     ▼
Scan D: E: F: G: H: I: J: for ParadiseTV.txt
     │
     ├── Found on flash drive ──► load from flash drive
     │
     └── Not found ──► load from Desktop
          │
          └── Desktop also missing ──► create default file + use fallback content
               │
               ▼
         Start ticker bar (bottom of screen, always on top)
         Start ad scheduler (first ad after 30 seconds, then every 5 minutes)
         Start file watcher (checks for changes every 5 seconds)
               │
               ▼
         Running indefinitely until stopped in IntelliJ
```

---

## Timing Reference

| Event | Timing |
|---|---|
| First ad appears | 30 seconds after launch |
| Ad interval | Every 5 minutes |
| Ad visible duration | 12 seconds |
| File watcher check | Every 5 seconds |
| Clock update | Every 1 second |

---

## Controls

| Action | How |
|---|---|
| Stop the program | Click red Stop button in IntelliJ |
| Update content | Edit `ParadiseTV.txt` and save — auto reloads |
| Change flash drive content | Edit file on flash drive — reloads within 5 seconds |

---

## Built With

- **Java 11+** — core language
- **Java Swing** — UI framework for overlay windows
- **Java AWT** — graphics and animation
- **javax.imageio** — image loading for ad panels
- **java.util.concurrent** — ad scheduling

---

## Author

Developed for **Paradise Club and Restaurant**  
Managed by **Mr. Michael Chetambe**

---

## License

Private project — for use at Paradise Club and Restaurant only.
