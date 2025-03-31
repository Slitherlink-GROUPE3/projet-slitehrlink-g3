# Cahier d'Analyse - Projet Slitherlink Groupe 3

## Introduction

Ce document présente l'analyse de la réalisation du projet Slitherlink par rapport au cahier des charges initial. Il détaille les fonctionnalités implémentées, celles partiellement développées et celles qui n'ont pas été réalisées.

## Analyse des fonctionnalités implémentées

### BF 01 : Système d'aide

| Fonctionnalité | Statut | Commentaires |
|----------------|--------|-------------|
| BF 01.1 : Aide par pop-up |  🟢️ Implémenté | Des instructions textuelles sont affichées dans le panneau d'instructions via les méthodes `addInstructionParagraph` et `addInstructionHighlight` |
| BF 01.2 : Aide par surlignage | 🔴️ Non implémenté | La méthode `highlightLine` permet de mettre en évidence des segments spécifiques de la grille |
| BF 01.3 : Aide visuelle | 🟡️ Implémenté uniquement dans le tutoriel | Des animations sont utilisées pour attirer l'attention (clignotement des lignes via `FadeTransition`) |
| BF 01.4 : Limite d'aide | 🟢️ Implémentée | Le nombre d'aides est compté avec la fonction checkCounter |
| BF 01.5 : Plusieurs types d'aide | 🟢️ Implémenté | Toutes les techniques prévues on été implémentées|

**Fonctionnement**: Les aides sont gérées par un système de compteur (`checkCounter`) dans `GameSaveManager.java`. Ce compteur est décrémenté à chaque utilisation d'une aide et est sauvegardé dans les métadonnées des parties. Les techniques d'aide sont implémentées dans des classes dédiées du package `com.tpgr3.Techniques` qui suivent toutes l'interface `Techniques` contenant deux méthodes principales: `estApplicable(Grille)` qui détecte si une technique peut être appliquée sur la grille actuelle, et `appliquer(Grille)` qui modifie la grille pour appliquer la technique. L'affichage visuel est géré par des transitions JavaFX comme `FadeTransition` pour faire clignoter les lignes suggérées.


### BF 02 : Système de score

| Fonctionnalité | Statut | Commentaires |
|----------------|--------|-------------|
| BF 02.1 : Calcul du score | 🟢️ Implémenté | Formule calculant le score : $$score = \frac{10}{\sqrt{temps + 0.5}} \times 1000 \times \left( 1 - (aides \times 0.1) \right)$$ |
| BF 02.2 : Chronomètre | 🟢️ Implémenté | Le temps est enregistré dans les sauvegardes (`SaveMetadata.elapsedTime`) |

**Fonctionnement**: Le chronomètre est implémenté via un timer dans `GameScene.java`. Le temps écoulé est stocké dans une variable et mis à jour régulièrement. Lors d'une sauvegarde, ce temps est enregistré dans l'objet JSON via `saveData.put("elapsedTime", seconds)` dans `GameSaveManager.saveGame()`. Ce temps est ensuite stocké dans les métadonnées de sauvegarde (`SaveMetadata.elapsedTime`). Il est également formaté pour affichage via la méthode `getFormattedTime()` qui convertit les secondes en format "MM:SS". Cependant, ce temps n'est pas utilisé pour calculer un score comme prévu dans le cahier des charges.


### BF 03 : Modes de jeu

| Fonctionnalité | Statut | Commentaires |
|----------------|--------|-------------|
| BF 03.1 : Grilles par difficulté |🟢️ Implémenté | Seulement les 2 premières lignes sont jouables, le reste est implémenté mais pas encore fonctionnel |
| BF 03.11 : Mode histoire | 🟢️ Implémenté | Mode histoire gérée par le fichier progression.json de chaque joueur |
| BF 03.12 : Mode libre | 🟢️ Implémenté | Choix d'une grille entre facile, moyen ou difficile |

**Fonctionnement**: Les modes de jeu sont implémentés via une architecture modulaire. Les grilles sont classées par difficulté (facile, moyen, difficile) et stockées dans le dossier "grids/" au format JSON. Le mode histoire suit une progression linéaire gérée par le fichier `progression.json` propre à chaque utilisateur, qui enregistre les niveaux complétés. Ce système permet de débloquer progressivement de nouvelles grilles à mesure que le joueur avance. Le mode libre, accessible depuis le menu principal, offre au joueur la possibilité de sélectionner n'importe quelle grille disponible parmi les différentes catégories de difficulté. La classe `GameScene` est responsable du chargement des grilles via la méthode `loadGridFromJson()`.

### BF 04 : Système de sauvegarde

| Fonctionnalité | Statut | Commentaires |
|----------------|--------|-------------|
| BF 04.1 : Sauvegarde par utilisateur | 🟢️ Implémenté  | La classe `GameSaveManager` permet des sauvegardes par utilisateur, stockées dans le dossier de chaque utilisateur |
| BF 04.2 : Sauvegarde automatique | 🟢️ Implémenté | Sauvegarde automatique en revenant au menu ou en quittant le jeu via les méthodes `saveGame()` et `autoSave()` |
| BF 04.3 : Sauvegarde globale | 🟢️ Implémenté | Le système de sauvegarde conserve l'état global du jeu |
| BF 04.4 : Sauvegarde des grilles | 🟢️ Implémenté | Des sauvegardes spécifiques aux grilles sont possibles mais ne sauvegarde pas si on recommence la partie (garde la grille finie) |

**Fonctionnement**: Les sauvegardes sont gérées par la classe `GameSaveManager.java` qui stocke les données dans le dossier propre à chaque utilisateur (`users/username/saves/`). Chaque sauvegarde contient l'état complet de la grille (lignes placées, croix) et les métadonnées (temps écoulé, nombre d'aides utilisées). Le système utilise le format JSON pour stocker ces informations avec une structure claire: les segments sont identifiés par leurs coordonnées, et leur état (ligne, croix ou vide) est enregistré. La sauvegarde automatique se déclenche lors du retour au menu ou à la fermeture du jeu via la méthode `autoSave()`. Le chargement d'une partie sauvegardée est effectué par `loadGame()` qui reconstruit l'état exact de la grille et restaure toutes les interactions précédentes. Le système vérifie également si une grille a été complétée pour éviter de charger des grilles terminées comme parties en cours.

### BF 05 : Vérification et retour

| Fonctionnalité | Statut | Commentaires |
|----------------|--------|-------------|
| BF 05.1 : Vérification automatique | 🟢️ Implémenté | La méthode `checkProgress` vérifie l'état de la grille |
| BF 05.2 : Vérification manuelle | 🟢️ Implémenté | Existe via `SlitherGridChecker` qui vérifie la validité de la solution |
| BF 05.3 : Retour arrière/avant | 🟢️ implémenté | On peut annuler et recommencer des coups |
| BF 05.4 : Fonctionnalité de suppression | 🟢️ Implémenté | Les lignes et croix peuvent être effacées | 

Le système de vérification est bien implémenté mais les fonctionnalités d'annulation/rétablissement semblent absentes.

**Fonctionnement**: La vérification est gérée par `SlitherGridChecker.java` qui analyse l'état actuel de la grille pour déterminer si la solution est valide. Elle vérifie que les contraintes des nombres sont respectées (nombre de segments autour de chaque case) et que la boucle est fermée et unique. La classe construit un graphe des segments actifs et vérifie la connectivité. La vérification manuelle est déclenchée par le bouton "Vérifier" qui appelle `handleCheckButton()` dans `GameScene`. L'historique des coups est géré via une liste `moveHistory` dans `SlitherGrid.java`.


### BF 06 : Interactions sur la grille

| Fonctionnalité | Statut | Commentaires |
|----------------|--------|-------------|
| BF 06.1 : Poser des bâtons | 🟢️ Implémenté | Via `drawLine` |
| BF 06.2 : Poser des croix | 🟢️ Implémenté | Via `placeCross` |
| BF 06.3 : Afficher l'aide | 🟢️ Implémenté | Les techniques d'aide sont accessibles |
| BF 06.4 : Placement auto de croix | 🟢️ Implémenté | Les croix sont automatiquement placées sur les segments inutilisables |
| BF 06.5 : Système d'hypothèses | 🔴️ Non implémenté | Pas de fonctionnalité de checkpoint |
| BF 06.7 : Recommencer la grille | 🟢️ Implémenté | Via la méthode `reinitialiser` de la classe `Grille` |

**Fonctionnement**: Les interactions sont gérées dans `SlitherGrid.java` qui capture les événements de souris sur les segments. La méthode `handleLineClick(MouseEvent e, Line line)` détecte le type de clic et appelle soit `drawLine()` soit `placeCross()`. Pour tracer un segment, `drawLine()` change la couleur du segment (`line.setStroke(Color.web(DARK_COLOR))`) et met à jour l'état interne de la grille. Pour placer une croix, `placeCross()` utilise `CrossMoveHelper.createCrossForSave()` qui crée deux lignes croisées sur le segment. La méthode `reinitialiser()` dans `Grille.java` réinitialise la grille à son état d'origine en recréant toutes les cellules. L'état de la grille est maintenu dans `SlitherGrid.gridLines` qui associe chaque identifiant de segment à son objet Line correspondant.


### BF 07 : Système d'identification

| Fonctionnalité | Statut | Commentaires |
|----------------|--------|-------------|
| Connexion pour sauvegardes | 🟢️ Implémenté | Sauveguarde la progression de tous les niveaux |

**Fonctionnement**: La gestion des utilisateurs est implémentée dans `UserManager.java` qui utilise un fichier texte simple "slitherlink_users.txt" pour stocker les noms d'utilisateurs. La classe fournit des méthodes pour ajouter un nouvel utilisateur (`addUser(String username)`), définir l'utilisateur actuel (`setCurrentUser(String username)`) et récupérer l'utilisateur actuel (`getCurrentUser()`). Lors du démarrage de l'application, le système vérifie si un utilisateur a déjà été créé et propose soit de se connecter avec un utilisateur existant, soit d'en créer un nouveau. 


### BF 08 : Tutoriel

| Fonctionnalité | Statut | Commentaires |
|----------------|--------|-------------|
| Tutoriel des techniques | 🟢️ Implémenté | La classe `TechniquesScene` implémente un tutoriel complet avec des étapes progressives |

**Fonctionnement**: Le tutoriel est implémenté dans `TechniquesScene.java` qui crée une interface guidée pour apprendre les techniques de Slitherlink. Il est structuré en étapes successives, chacune présentant une technique spécifique. Pour chaque étape, la classe utilise `showStep(int step)` qui affiche les instructions correspondantes et configure la grille de démonstration. Le tutoriel utilise des méthodes comme `addInstructionParagraph()` et `addInstructionHighlight()` pour afficher des textes explicatifs, et `highlightLine()` pour mettre en évidence les segments pertinents. L'utilisateur peut interagir avec la grille pour appliquer la technique présentée, et sa progression est vérifiée par des méthodes comme `checkThreeAdjacentProgress()`. Un bouton "Suivant" permet de passer à l'étape suivante une fois la technique maîtrisée.


### BF 09 : Paramètres

| Fonctionnalité | Statut | Commentaires |
|----------------|--------|-------------|
| Options du jeu | 🟡️ Partiellement implémenté | La page des paramètres permet de changer le mode de couleur (sombre ou clair), de sauvegarder, de changer de compte et de quitter et d'aller au menu principal. Cependant le mode sombre ne dure pas après la page des paramètres |

**Fonctionnement**: La page des paramètres est implémentée dans `SettingScene.java` qui offre plusieurs fonctionnalités importantes: le changement de compte utilisateur via un bouton dédié qui renvoie l'utilisateur à l'écran de connexion, permettant ainsi la gestion de plusieurs profils avec leurs sauvegardes respectives; la gestion des thèmes avec un mode clair et sombre via `applyTheme(boolean darkMode)`, bien que cette fonctionnalité ne persiste pas entre les écrans; et la navigation vers d'autres sections de l'application comme le menu principal. Le système de changement d'utilisateur est directement lié au `UserManager` qui gère les profils et leurs données associées stockées dans le dossier `users/`.

