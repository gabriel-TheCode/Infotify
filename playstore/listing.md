# Play Store listing copy

Ready to paste into the Play Console. Two locales: English (default) and French.

Every claim here is checked against the code rather than written from memory —
fifteen topics with a cap of five (`Topic.MAX_SELECTED`), four regions (`Region`),
nine languages (`Language`), three permissions in the manifest, and no analytics or
advertising library anywhere in `app/src/main`. Keep it that way: if the app changes,
this file changes with it, and so does https://infotify.nativia.co/privacy.

---

## English — default listing

### App name (30 max)

```
Infotify: News Without Noise
```

Alternative, if you would rather lead with the brand alone:

```
Infotify — Daily News Brief
```

### Short description (80 max)

```
Choose your subjects. One briefing a day. No account, no tracking.
```

### Full description (4000 max)

```
Infotify is a news reader built around one idea: you decide what is on your front page.

CHOOSE YOUR SUBJECTS
Pick up to five from fifteen — world, politics, business, technology, science, health, sport, entertainment, environment, education, crime, food, travel and lifestyle. Add a region if you want coverage from Africa, Europe, the Americas or Asia-Pacific. Change them whenever you like, in two taps.

ONE BRIEFING A DAY
A single notification, at a time you choose, and only when something new has appeared in your subjects. Not a stream of alerts. Turn it off entirely and the app works exactly the same.

READ NOW OR READ LATER
Save any story for when you have time. Saved articles live on your phone and open without a connection. Recently loaded headlines are cached too, so the app opens with something to read on a bad train line.

SEARCH THE WHOLE WIRE
One query across thousands of publishers, in nine languages: English, French, Spanish, German, Italian, Portuguese, Dutch, Russian and Arabic.

MADE FOR READING
A true dark theme rather than a dimmed white page. Typography chosen for headlines, not for interfaces. A layout that gives the top story more weight than the rest. Infotify follows your system light or dark setting, or you can pin either one.

NO ACCOUNT. NO TRACKING.
There is nothing to sign up for. Infotify never asks for a name, an email address or a phone number, and has nowhere to put one. There is no analytics library in the app and no advertising. Your subjects, your saved articles and your settings stay in the app's own storage on your device — uninstalling removes all of it, because there is no copy anywhere else.

Articles open on the publisher's own site, so their work is read where they published it.

Privacy policy: https://infotify.nativia.co/privacy
Support: https://infotify.nativia.co/support

Published by Nativia Solutions.
```

---

## Français

### Nom de l'application (30 max)

```
Infotify : l'info sans bruit
```

### Description courte (80 max)

```
Vos sujets, un briefing par jour. Sans compte, sans pistage.
```

### Description complète (4000 max)

```
Infotify est un lecteur d'actualités construit autour d'une idée : c'est vous qui décidez de ce qui fait votre une.

CHOISISSEZ VOS SUJETS
Cinq sujets au maximum, parmi quinze — monde, politique, économie, technologie, science, santé, sport, culture, environnement, éducation, justice, gastronomie, voyage et art de vivre. Ajoutez une région pour suivre l'Afrique, l'Europe, les Amériques ou l'Asie-Pacifique. Vous les modifiez quand vous voulez, en deux gestes.

UN BRIEFING PAR JOUR
Une seule notification, à l'heure que vous fixez, et uniquement quand quelque chose de neuf est paru dans vos sujets. Pas un flux d'alertes. Vous pouvez la désactiver entièrement : l'application fonctionne exactement pareil.

LIRE MAINTENANT OU PLUS TARD
Enregistrez un article pour le lire quand vous avez le temps. Vos articles enregistrés restent sur votre téléphone et s'ouvrent sans connexion. Les titres récemment chargés sont aussi mis en cache : l'application s'ouvre avec de quoi lire, même dans un train.

CHERCHEZ DANS TOUTE LA DÉPÊCHE
Une seule requête sur des milliers de sources, en neuf langues : français, anglais, espagnol, allemand, italien, portugais, néerlandais, russe et arabe.

FAIT POUR LIRE
Un vrai thème sombre, pas une page blanche assombrie. Une typographie choisie pour des titres, pas pour des interfaces. Une mise en page qui donne plus de poids à l'article principal qu'au reste. Infotify suit le réglage clair/sombre de votre système, ou vous fixez l'un des deux.

SANS COMPTE. SANS PISTAGE.
Il n'y a rien à créer. Infotify ne demande jamais de nom, d'adresse e-mail ni de numéro de téléphone, et n'a nulle part où les mettre. Aucune bibliothèque d'analyse dans l'application, aucune publicité. Vos sujets, vos articles enregistrés et vos réglages restent dans le stockage de l'application, sur votre appareil — désinstaller supprime tout, parce qu'il n'existe aucune copie ailleurs.

Les articles s'ouvrent sur le site de l'éditeur : leur travail se lit là où ils l'ont publié.

Politique de confidentialité : https://infotify.nativia.co/privacy
Assistance : https://infotify.nativia.co/support

Publié par Nativia Solutions.
```

---

## Store settings, both locales

| Field | Value |
|---|---|
| Category | News & Magazines / Actualités et magazines |
| Website | https://infotify.nativia.co |
| Privacy policy | https://infotify.nativia.co/privacy |
| Support email | tekombo.gabriel@gmail.com |
| Content rating | Everyone — but the questionnaire must declare that the app shows news from third-party publishers, whose content is not moderated |

The privacy policy URL is the one that still has to change in the console: it points at
the GitHub repository, and Play rejects a listing whose policy URL is dead or unrelated.

## Data safety

What the code supports saying: **the app itself collects nothing.** No analytics SDK, no
advertising, no identifier, and three permissions — `INTERNET`, `ACCESS_NETWORK_STATE`,
`POST_NOTIFICATIONS` — none of which is in a sensitive group.

One question is genuinely open. `infotify-api.nativia.co` keeps a rate-limit counter per
hashed IP address for under a day. Play only exempts data handled *ephemerally* — in
memory, never written — and a file that survives a day is not that. Play also offers no
"IP address" category to declare it under. This is a judgement call worth putting to Play
support rather than guessing at; the privacy policy already describes the behaviour
plainly, which is what matters if it is ever questioned.
