# Projet de génie logiciel et gestion de projet (INFO-F-307)

Cette application a été développée dans le cadre du cours "Génie logiciel et gestion de projet Info-F307". Il s'agit d'apprendre la méthodoligie "Extreme Programming" (XP) ainsi que le "Test Driven Developpment" (TDD). Ce projet est réalisé en Java par un grouppe de 8 étudiants de l'ULB.

# Utilisation

Le projet utilise la version 1.8 du JDK, qui inclut la librairie JavaFX ainsi que Maven pour pouvoir télécharger les dépendances tels que JUnit, ControlsFX et jdbcSQlite. L'objectif principal du projet est d'implémenter un gestionnaire de projets personnels facilement intégrable avec des outils externes. Le logiciel permettra de créer un projet
(éventuellement divisible en sous-projets), y ajouter des tâches atomiques avec des caractéristiques (durée, avancement,...), ainsi que d'attribuer des dates de début et de fin
aux tâches pour définir des priorités. Le logiciel devra aussi permettre de visualiser graphiquement l'état d'avancement d'un projet, les priorités et les différentes échéances.
En plus de ces fonctionnalités de base, le logiciel permettra aussi de gérer plusieurs utilisateurs et plusieurs projets par utilisateur, tout en gardant un niveau de sécurité adéquat, ainsi qu'extraire des statistiques sur l'état d'avancement d'un projet (par exemple une comparaison entre le temps de réalisation réel et le temps estimé sur base des projets déjà terminés (pour évaluer l'estimation correcte du temps de réalisation des tâches)).

## Démarrage 

Le démarrage peut se faire via l'IDE IntelliJ ou via le fichier .jar directement.Pour pouvoir utiliser l'application il faut disposer du JRE8 disponible ici: https://www.java.com/fr/download/. Une fois que tout est installé vous pouvez démarrer le .jar avec un simple double-click. Le fichier .jar se trouve dans le dossier /dist. Notons qu'il est nécessaire de disposer d'un dossier /src qui contient la base de données dans le répertoire du .jar. Il est important de noter que la base de données doit s'appeler "Database.db" pour assurer le bon fonctionnement de l'application. 

## Configuration :

En lançant IntelliJ ouvrez le dossier du projet via l'archive téléchargée ou en clonant le dépôt distant. 
![openProject](https://user-images.githubusercontent.com/44466591/112011705-ce3d2b00-8b28-11eb-84e7-5a33ea1e507b.png)

Afin de démarrer le programme il est nécessaire d'ajouter une configuration "Application" qui utilise Java 1.8 et de séléctionner la classe "Main" comme Main Class.
![Main_config](https://user-images.githubusercontent.com/44466591/112011075-30e1f700-8b28-11eb-9115-0f73be64bc5a.png)
Une fois que cela est fait vous pouvez démarrer l'application en séléctionnant la configuration crée dans la liste déroulante en haut à droite et en clickant sur le triangle verte juste à côté.

## Compilation

Le projet est compilé via l'IDE IntelliJ. IntelliJ a une option "Build" qui compile le code source en un fichier .jar qu'il est possible de lancer en dehors de l'IDE. Par défault, le fichier .jar se trouvera dans le dossier <pathToApplication>\out\artifacts.

![artifact1](https://user-images.githubusercontent.com/44466591/112015094-caf76e80-8b2b-11eb-9594-6e607e833d91.png)
![artifact2](https://user-images.githubusercontent.com/44466591/112013800-9f27b900-8b2a-11eb-87ff-1fc588268ebb.png)
![artifact3](https://user-images.githubusercontent.com/44466591/112013803-9f27b900-8b2a-11eb-90cf-8c1706f1d484.png)
![artifact7](https://user-images.githubusercontent.com/44466591/112016097-afd92e80-8b2c-11eb-927e-b99279a2db2f.png)
![artifact5](https://user-images.githubusercontent.com/44466591/112013805-9fc04f80-8b2a-11eb-8624-409468a1f69b.png)
![artifact6](https://user-images.githubusercontent.com/44466591/112013796-9cc55f00-8b2a-11eb-88a2-4b9fa20bce1c.png)

# Tests

Les test peuvent être lancés via le dossier "test" du projet dans IntelliJ. Pour cela il faut faire un click droit sur le dossier \tests dans IntelliJ et séléctionner l'option "Run All Tests". Dans la console on pourra observer quels tests ont échoués et lesquels sont réussis. 
![launch_tests](https://user-images.githubusercontent.com/44466591/112010457-9da8c180-8b27-11eb-9e1a-4dac480c82c6.png)

# Misc
Pour le contributeur psanou, il y a un problème à l'affichage du nombre de commits dans les insights section contributors. Vous pouvez retrouver le nombre réel de commits (75) à l'adresse suivante : https://github.com/ULB-INFOF307/2021-groupe-9/commits?author=psanou
## Développement

## Screenshot

## License
