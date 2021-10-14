
# Histoires
Informations récapitulatives concernant les différentes histoires.

#### Quelques précisions
Un point correspond à une heure de travail par binôme (approximatif). Par itération il faut accomplir 70 points.

----------------------


## Pondération

| Priorité/3 | N° | Description | Difficulté/3 | Risque/3 | Heures/158 | Points |
| ------ | ------ | ------ | ------ | ------ | ------ | ------ |
| 1 | [1](#Histoire-1) | H-1: Créer un utilisateur, login et mot de passe | 2 | 2 | 50 | 25 |
| 2 | [2](#Histoire-2) | H-2: Créer un projet et des sous-projets à un projet | 3 | 2 | 56 | 28 |
| 2 | [3](#Histoire-3) | H-3: Prioritisation des tâches | 3 | 3 | 30 | 15 |
| 2 | [4](#Histoire-4) | H-4: Calendrier des tâches | 2 | 2 | 60 | 30 |
| 2 | [5](#Histoire-5) | H-5: Versioning | 1 | 1 | 142 | 71 |
| 2 | [6](#Histoire-6) | H-6: Collaboration pour un projet | 3 | 2 | 60 | 30 |
| 2 | [7](#Histoire-7) | H-7: Exportation et importation de projet | 3 | 2 | 48 | 24 |
| 2 | [8](#Histoire-8) | H-8: Dashboard & Statistiques | 2 | 2 | 50 | 25 |
| 2 | [9](#Histoire-9) | H-9: Intégration avec des services cloud existants | 2 | 2 | 62 | 31 |
| 3 | [10](#Histoire-10) |  H-10: Rappels pour les projets avec échéance | 2 | 2 | 60 | 30 |
| 3 | [11](#Histoire-11) | H-11: Section d'aide | 3 | 3 | 30 | 15 |
| 3 | [12](#Histoire-12) | H-12: Sécurité des données | 1 | 1 | 46 | 23 |
| 3 | [13](#Histoire-13) | H-13: Intégrité des données | 1 | 1 | 42 | 21 |

----------------------


## Tableau Risque-priorité

| &#8595; Priorité / Risque &#8594; | 1 | 2 | 3 |
| ------ | ------ | ------ | ------ |
| 1 |  | 1 |  |
| 2 | 5 | 2, 4, 6, 7, 8, 9 | 3 |
| 3 | 12, 13 | 10 | 11 |

----------------------

## Description

### Histoire 1

**Instructions originales:**

- Pouvoir créer un utilisateur et se connecter avec le mot de passe (username, Nom, Prénom, email, mot de passe)
- Pouvoir modifier les informations de l'utilisateur dans l'espace profil
- Confirmer les données avant tout changement
- L'application fonctionne sans serveur (client-only)

**Tâches en plus:**          

:question: **Question:**       
- Origine des conditions d'utilisation ?
- Comment stocker les données ?

----------------------

## Description

### Histoire 2

**Instructions originales:**

- Utilisateur peut créer des projets et sous-projets
- Un projet est constitué de tâches, tags et sous-projets
- Un projet a une date de fin
- Permettre à l'utilisateur de modifier ses projets
- Permettre à l'utilisateur de supprimer ses projets

**Tâches en plus:**          

:question: **Question:**       
- Qu'est ce qu'un projet ?
- Que signifie "montrer la description et les étiquettes" ?

----------------------

## Description

### Histoire 3

**Instructions originales:**

- Ajouter une date de début et de fin aux tâches
- Ajouter une date de début aux projets
- Permettre de définir explicitement une durée aux tâches
- Trier les tâches en fonction de leur priorité dans le temps

**Tâches en plus:**          


:question: **Question:** 


----------------------

## Description

### Histoire 4

**Instructions originales:**

- Visualiser les tâches de un ou plusieurs projets dans un calendrier
- Utilisateur peut choisir les couleurs pour un projet

**Tâches en plus:**          

:question: **Question:**       
- Sous projet a la même couleur que le projet parent ?
- Comment afficher s’il n'y a pas de date pour une tâche ?


----------------------

## Description

### Histoire 5

**Instructions originales:**

- Versioning à l'intérieur de l'application :
  - add.
  - remove.
  - branch.
  - commit.
  - revert.
  - merge.
  - diff.

**Tâches en plus:**          

:question: **Question:**       
- Est-ce qu'on peut utiliser une librairie / git ?



----------------------

## Description

### Histoire 6

**Instructions originales:**

- Ajout de collaborateurs à un projet et tous ses sous-projets
- Ajout d'un système de notifications (avec Accepter/Refuser)

**Tâches en plus:**          

:question: **Question:**       
- Faut-il notifier l'inviteur ?
- "Bien visible" dans la liste des tâches de l'application ou bien dans la partie calendrier ?

----------------------

## Description

### Histoire 7

**Instructions originales:**

- Exportation de projets
- Importation de projets

**Tâches en plus:**          

:question: **Question:**    


----------------------

## Description

### Histoire 8

**Instructions originales:**

- Afficher les statistiques d'un ou de plusieurs projets

**Tâches en plus:**          

:question: **Question:**       

----------------------

## Description

### Histoire 9

**Instructions originales:**

- Pouvoir enregistrer les projets individuels sur le cloud

**Tâches en plus:**          

:question: **Question:**  

- Quelle API ? 
- Doit-on définir une limite de stockage ou est-ce pour illustrer ?

----------------------

## Description

### Histoire 10

**Instructions originales:**

- Rappels pour les projets à échéance au sein de l'application ou sur une application calendrier tierce.

**Tâches en plus:**          

:question: **Question:**    

- D'est quoi le délai pour le rappel d'échéance d'un projet ?
- Quelle méthode préférée le client ?

----------------------

## Description

### Histoire 11

**Instructions originales:**

- Créer une section d'aide au sein de l'application

**Tâches en plus:**          

:question: **Question:** 

- Quel format préféré le client ?

----------------------

## Description

### Histoire 12

**Instructions originales:**

- Le logiciel doit garantir que les données ne soient pas accessibles en dehors de l'application.
- Permettre de sécurisé l'exportation par mot de passe.
- L'importation requiert aussi le mot de passe

**Tâches en plus:**          

:question: **Question:**       

----------------------

## Description

### Histoire 13

**Instructions originales:**

- Logiciel doit garantir qu'on ne puisse pas altérer les données depuis l'extérieur.

**Tâches en plus:**          

:question: **Question:**      

- Besoin d'éclaircissement sur la question  


