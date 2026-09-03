# Play Store assets

Everything Google Play asks for on the store listing, plus the script that produces it.

| File | Size | Play field |
|---|---|---|
| `icon-512x512.png` | 512 × 512 | App icon |
| `feature-graphic-1024x500.png` | 1024 × 500 | Feature graphic |
| `screenshot-1-explore.png` … `screenshot-8-settings.png` | 1080 × 1920 | Phone screenshots |

## Regenerating

```bash
python3 playstore/generate.py
```

Needs Pillow. It reads the raw captures from `captures/`, the fonts from
`app/src/main/res/font/`, and overwrites the assets in place.

## How these are made, and what stays true

- **The icon is the launcher icon, rendered — not redrawn.** `generate.py` takes its geometry
  from `app/src/main/res/drawable/ic_launcher_foreground.xml` (stem 41.5–60.5 × 52–82, dot at
  (60, 34) r 9.5, on a 108-unit viewport), so the store icon and the installed icon are the
  same drawing. They were not before: the previous 512 was a separate drawing whose stem sat
  15 px off-axis and 50 px too tall.
- **The device chassis is drawn around each capture, never baked into it.** Replacing a
  screenshot means dropping a new PNG into `captures/` and re-running the script; no frame
  has to be redrawn, and no capture is ever stretched — the phone width is fixed and the
  height follows the file's own ratio.
- **The captures are real.** Nothing is mocked up or retouched. They come from the
  `releaseTest` build — the R8-minified one — running on a Pixel 3a emulator (1080 × 2220,
  API 34) against the live `infotify-api.nativia.co` proxy, with SystemUI demo mode on so
  the status bar reads 9:00 with a full battery instead of leaking a real device's state.
- **Only the gesture pill is cropped** (85 px, measured). The navigation bar and its labels
  stay: they are the app's own structure, and they are what a store visitor reads the layout
  from.

## Known gaps

- **Tablet screenshots are not here.** Play wants 7-inch and 10-inch sets before it will show
  the app as tablet-ready. They have to be captured on tablet emulators; nothing here can be
  upscaled into them honestly.
- **`screenshot-2-for-you.png` shows one empty grey thumbnail.** That is the app's real
  current behaviour, not a capture artefact: a publisher image that never resolves leaves the
  placeholder box in place instead of collapsing the way `ArticleCard` intends. Worth fixing
  before this screenshot is final — see the note in the commit that added these files.
- **The screenshots are English only.** The app ships `values-fr` as well; a French set means
  re-running the walkthrough with `adb shell cmd locale set-app-locales com.thecode.infotify
  --locales fr-FR` and a translated `SHOTS` table.
