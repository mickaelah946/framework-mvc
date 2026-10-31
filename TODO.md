# TODO — Gestion commande fournisseur, réceptions et remplissage des compartiments

## 1. Objectif général

Mettre en place un workflow complet pour suivre les poussins/poulets depuis la commande fournisseur jusqu'à leur installation dans les compartiments du poulailler.

Flux principal :

**Bon de commande fournisseur → Réception 1 à l'aéroport → Réception 2 au poulailler → Famenona compartiment → Historique**

Chaque étape doit être liée à l'étape précédente afin de pouvoir contrôler les écarts de quantité.

---

# 2. Bon de commande fournisseur

## 2.1. Créer le formulaire "Bon de commande fournisseur"

- [ ] Créer une page/formulaire `Bon de commande fournisseur`
- [ ] Le bon doit concerner l'achat/importation de poussins ou poulets venant d'un fournisseur extérieur
- [ ] Ne pas faire de personnalisation visuelle spécifique au client
- [ ] Générer un numéro unique de bon de commande
- [ ] Ajouter la date de création
- [ ] Ajouter le statut du bon :
  - [ ] Brouillon
  - [ ] Validé
  - [ ] Expédié
  - [ ] Arrivé à l'aéroport
  - [ ] Réceptionné au poulailler
  - [ ] Terminé

## 2.2. Champs du bon de commande fournisseur

- [ ] Fournisseur
- [ ] Race du poulet / poussin
- [ ] Âge
- [ ] Nombre demandé
- [ ] Prix unitaire
- [ ] Prix total calculé automatiquement
- [ ] Destination / lieu où les poussins doivent aller
- [ ] Pays de destination
- [ ] Aéroport de réception
- [ ] Poulailler de destination
- [ ] Date et heure prévue de départ
- [ ] Date et heure réelle de départ
- [ ] Date et heure prévue d'arrivée à l'aéroport
- [ ] Date et heure réelle d'arrivée à l'aéroport
- [ ] Commentaire / remarque

## 2.3. Gestion de l'âge

L'âge doit pouvoir être saisi en **jours**.

Règles d'affichage :

- [ ] Si l'âge est inférieur à 7 jours, afficher uniquement les jours  
  Exemple : `5 jours`
- [ ] Si l'âge est exactement 7 jours, afficher : `7 jours (1 semaine)`
- [ ] Si l'âge est supérieur ou égal à 7 jours, afficher les jours + l'équivalent en semaines
- [ ] Exemple : `14 jours (2 semaines)`
- [ ] Exemple : `18 jours (2 semaines + 4 jours)`

Le système doit conserver la valeur exacte en jours dans la base.

## 2.4. Calculs

- [ ] Calcul automatique :

`prix_total = nombre_commandé × prix_unitaire`

- [ ] Empêcher les valeurs négatives
- [ ] Le nombre commandé doit être supérieur à 0
- [ ] Le prix unitaire doit être supérieur ou égal à 0

## 2.5. Validation du bon

Boutons :

- [ ] `Enregistrer`
- [ ] `Valider`

Lors du clic sur `Valider` :

- [ ] Vérifier les champs obligatoires
- [ ] Verrouiller les informations principales du bon
- [ ] Ajouter le bon dans l'historique
- [ ] Autoriser ensuite la création de la Réception 1
- [ ] Conserver la date et l'heure de validation
- [ ] Conserver l'utilisateur ayant validé

---

# 3. Historique des bons de commande

Créer une page `Historique des bons de commande`.

Afficher au minimum :

- [ ] Numéro du bon
- [ ] Fournisseur
- [ ] Race
- [ ] Âge
- [ ] Nombre commandé
- [ ] Prix unitaire
- [ ] Prix total
- [ ] Destination
- [ ] Date/heure de départ
- [ ] Date/heure d'arrivée à l'aéroport
- [ ] Statut
- [ ] Bouton `Voir détail`

Filtres :

- [ ] Par date
- [ ] Par fournisseur
- [ ] Par race
- [ ] Par destination
- [ ] Par statut

---

# 4. Réception 1 — Aéroport

## 4.1. Principe

La Réception 1 correspond au premier inventaire effectué lorsque les poussins/poulets arrivent à l'aéroport.

Exemple :

- Bon fournisseur : 1 000 poussins
- Morts à l'arrivée : 10
- Perdus/manquants : 5
- Vivants disponibles : 985

Le système doit automatiquement comparer la réception avec le bon fournisseur.

## 4.2. Formulaire Réception 1

- [ ] Créer une page/formulaire `Réception 1 - Aéroport`
- [ ] Sélectionner un bon de commande fournisseur validé
- [ ] Une réception ne doit pas être créée sans bon de commande validé

### Informations du bon à afficher en lecture seule

- [ ] Numéro du bon
- [ ] Fournisseur
- [ ] Race
- [ ] Âge
- [ ] Quantité commandée
- [ ] Destination
- [ ] Date/heure de départ
- [ ] Date/heure prévue d'arrivée

Ces données ne doivent pas être modifiables depuis la réception.

## 4.3. Champs Réception 1

- [ ] Date et heure réelle de réception à l'aéroport
- [ ] Nombre de poussins/poulets morts
- [ ] Nombre de poussins/poulets perdus / manquants
- [ ] Nombre vivant calculé automatiquement
- [ ] Remarque

Calcul :

`vivants_r1 = quantité_commandée - morts_r1 - perdus_r1`

## 4.4. Contrôle de cohérence

- [ ] Vérifier que :

`morts_r1 + perdus_r1 + vivants_r1 = quantité_commandée`

- [ ] Afficher clairement :
  - [ ] Conforme
  - [ ] Non conforme

- [ ] Si non conforme, afficher l'écart
- [ ] Interdire les nombres négatifs
- [ ] Interdire un total supérieur à la quantité commandée

## 4.5. Actions

Boutons :

- [ ] `Enregistrer`
- [ ] `Valider`

Lors de la validation :

- [ ] Verrouiller la Réception 1
- [ ] Enregistrer l'heure de validation
- [ ] Enregistrer l'utilisateur ayant validé
- [ ] Mettre à jour le statut du bon
- [ ] Autoriser la création de la Réception 2

---

# 5. Réception 2 — Poulailler

## 5.1. Principe

La Réception 2 est effectuée lorsque les poussins/poulets arrivent finalement au poulailler.

Il faut recompter :

- les vivants ;
- les morts constatés à l'arrivée au poulailler.

Cela permet de détecter les décès survenus pendant le trajet entre l'aéroport et le poulailler.

## 5.2. Formulaire Réception 2

- [ ] Créer une page/formulaire `Réception 2 - Poulailler`
- [ ] La Réception 2 doit obligatoirement être liée à une Réception 1 validée

### Réception antécédente

- [ ] Conserver l'identifiant de la Réception 1
- [ ] Cette relation ne doit jamais être modifiable
- [ ] L'identifiant technique peut être caché dans l'interface
- [ ] Afficher les informations utiles de la Réception 1 en lecture seule

Informations à afficher :

- [ ] Numéro du bon fournisseur
- [ ] Race
- [ ] Quantité commandée
- [ ] Morts à l'aéroport
- [ ] Perdus à l'aéroport
- [ ] Vivants après Réception 1
- [ ] Date/heure de Réception 1

## 5.3. Champs Réception 2

- [ ] Date et heure d'arrivée au poulailler
- [ ] Nombre vivant à l'arrivée au poulailler
- [ ] Nombre mort constaté pendant/après le trajet
- [ ] Remarque

## 5.4. Contrôle du trajet

Calcul attendu :

`morts_trajet = vivants_r1 - vivants_r2`

Si l'utilisateur saisit lui-même le nombre de morts à la Réception 2 :

- [ ] Vérifier que :

`vivants_r2 + morts_r2 = vivants_r1`

- [ ] Afficher `Aucun décès supplémentaire` si `morts_r2 = 0`
- [ ] Afficher le nombre de décès supplémentaires si `morts_r2 > 0`
- [ ] Signaler une anomalie si `vivants_r2 > vivants_r1`
- [ ] Interdire un total incohérent

## 5.5. Actions

Boutons :

- [ ] `Enregistrer`
- [ ] `Valider`

Lors de la validation :

- [ ] Verrouiller la Réception 2
- [ ] Conserver la date/heure de validation
- [ ] Conserver l'utilisateur ayant validé
- [ ] Mettre à jour le statut du bon
- [ ] Autoriser le `Famenona compartiment`

---

# 6. Famenona compartiment

## 6.1. Principe

Après la Réception 2, les poussins/poulets vivants doivent être répartis dans les différents compartiments du poulailler.

Les mâles et les femelles doivent pouvoir être séparés.

Une même Réception 2 peut donc produire plusieurs lignes de répartition.

Exemple :

- Compartiment A → 200 mâles
- Compartiment B → 220 femelles
- Compartiment C → 150 mâles
- etc.

## 6.2. Formulaire "Famenona compartiment"

- [ ] Créer une page/formulaire `Famenona compartiment`
- [ ] Le formulaire doit être lié à une Réception 2 validée

### Réception antécédente

- [ ] Conserver l'identifiant de la Réception 2
- [ ] Cette liaison ne doit jamais être modifiable
- [ ] Elle peut être cachée techniquement
- [ ] Afficher en lecture seule :
  - [ ] Numéro du bon
  - [ ] Race
  - [ ] Âge
  - [ ] Quantité vivante disponible
  - [ ] Poulailler
  - [ ] Date de Réception 2

## 6.3. Champs d'une ligne de remplissage

- [ ] Liste déroulante `Compartiment`
- [ ] Afficher automatiquement la capacité maximale du compartiment
- [ ] Nombre d'akoho / nombre de poulets
- [ ] Sexe :
  - [ ] Mâle
  - [ ] Femelle
- [ ] Santé de 1 à 10
- [ ] Remarque optionnelle

## 6.4. Règles de capacité

- [ ] Chaque compartiment possède une capacité maximale
- [ ] Afficher la capacité totale
- [ ] Afficher le nombre déjà occupé
- [ ] Afficher la capacité restante

Calcul :

`capacité_restante = capacité_max - quantité_déjà_affectée`

- [ ] Empêcher d'ajouter un nombre supérieur à la capacité restante
- [ ] Empêcher une valeur inférieure ou égale à 0

## 6.5. Gestion des sexes

- [ ] Permettre de séparer mâles et femelles
- [ ] Chaque ligne de répartition doit enregistrer le sexe
- [ ] Le même lot peut être réparti dans plusieurs compartiments
- [ ] Un compartiment peut recevoir un sexe déterminé selon les règles métier

Option recommandée :

- [ ] Ajouter dans la configuration du compartiment :
  - [ ] Sexe autorisé : mâle / femelle / mixte

## 6.6. Contrôle de quantité globale

Le nombre total réparti ne doit pas dépasser le nombre de vivants de la Réception 2.

Calcul :

`total_affecté = somme de toutes les lignes de famenona`

`reste_à_affecter = vivants_r2 - total_affecté`

Afficher en permanence :

- [ ] Vivants disponibles
- [ ] Total déjà affecté
- [ ] Reste à affecter

- [ ] Interdire `total_affecté > vivants_r2`
- [ ] Signaler si tous les animaux ne sont pas encore affectés

## 6.7. Santé

- [ ] Champ entier de 1 à 10
- [ ] Valeur minimale : 1
- [ ] Valeur maximale : 10

Interprétation possible :

- 1 = très mauvais état
- 10 = excellent état

## 6.8. Boutons

### Bouton `Enregistrer`

- [ ] Ajouter la ligne de répartition sans clôturer le formulaire
- [ ] Permettre de saisir ensuite un autre compartiment
- [ ] Mettre à jour le total affecté
- [ ] Mettre à jour le reste à affecter

### Bouton `Valider`

- [ ] Valider définitivement le remplissage des compartiments
- [ ] Vérifier toutes les capacités
- [ ] Vérifier les quantités
- [ ] Vérifier les sexes
- [ ] Vérifier les notes de santé
- [ ] Conserver l'utilisateur ayant validé
- [ ] Conserver la date/heure de validation
- [ ] Passer le dossier au statut `Terminé`

Option métier :

- [ ] Décider si la validation est autorisée uniquement lorsque :

`total_affecté = vivants_r2`

ou si une validation partielle est autorisée.

---

# 7. Gestion des compartiments

Créer un écran de configuration des compartiments.

Champs :

- [ ] Nom / numéro du compartiment
- [ ] Poulailler
- [ ] Capacité maximale
- [ ] Capacité actuellement utilisée
- [ ] Capacité restante calculée
- [ ] Sexe autorisé :
  - [ ] Mâle
  - [ ] Femelle
  - [ ] Mixte
- [ ] Actif / inactif

---

# 8. Traçabilité complète du lot

Créer une vue détail permettant de voir tout le parcours d'un lot.

Exemple :

## Commande

- Fournisseur
- Race
- Âge
- Quantité commandée
- Prix
- Destination

↓

## Expédition

- Heure de départ
- Heure d'arrivée à l'aéroport

↓

## Réception 1

- Quantité commandée
- Morts
- Perdus
- Vivants

↓

## Réception 2

- Vivants venant de l'aéroport
- Morts pendant le trajet
- Vivants au poulailler

↓

## Famenona compartiment

- Compartiment
- Nombre
- Sexe
- Santé
- Capacité

---

# 9. Historique et audit

Pour chaque étape, conserver :

- [ ] Date de création
- [ ] Créé par
- [ ] Date de modification
- [ ] Modifié par
- [ ] Date de validation
- [ ] Validé par
- [ ] Statut
- [ ] Référence de l'étape précédente

Les données validées importantes ne doivent plus être modifiables directement.

Prévoir si nécessaire :

- [ ] Annulation
- [ ] Correction avec motif
- [ ] Journal des changements

---

# 10. Règles de cohérence globales

## Commande → Réception 1

`commandé = morts_r1 + perdus_r1 + vivants_r1`

## Réception 1 → Réception 2

`vivants_r1 = morts_r2 + vivants_r2`

## Réception 2 → Compartiments

`vivants_r2 >= total_affecté_compartiments`

À la clôture complète du lot :

`vivants_r2 = total_affecté_compartiments`

---

# 11. Pages / écrans à créer

- [ ] Liste des bons de commande fournisseur
- [ ] Nouveau bon de commande fournisseur
- [ ] Détail d'un bon fournisseur
- [ ] Historique des bons
- [ ] Réception 1 - Aéroport
- [ ] Historique Réception 1
- [ ] Réception 2 - Poulailler
- [ ] Historique Réception 2
- [ ] Famenona compartiment
- [ ] Historique des remplissages
- [ ] Gestion des compartiments
- [ ] Vue complète de traçabilité du lot

---

# 12. Modèles de données recommandés

## FournisseurOrder

- id
- numero_bon
- fournisseur_id
- race_id
- age_jours
- quantite_commandee
- prix_unitaire
- prix_total
- pays_destination
- aeroport_destination
- poulailler_destination_id
- date_depart_prevue
- date_depart_reelle
- date_arrivee_prevue
- date_arrivee_reelle
- statut
- created_at
- created_by
- validated_at
- validated_by

## ReceptionAirport

- id
- fournisseur_order_id
- date_reception
- nb_morts
- nb_perdus
- nb_vivants
- conforme
- ecart
- remarque
- statut
- created_at
- created_by
- validated_at
- validated_by

## ReceptionPoulailler

- id
- reception_airport_id
- date_reception
- nb_morts_trajet
- nb_vivants
- remarque
- statut
- created_at
- created_by
- validated_at
- validated_by

## Compartiment

- id
- poulailler_id
- nom
- capacite_max
- sexe_autorise
- actif

## FamenonaCompartiment

- id
- reception_poulailler_id
- statut
- created_at
- created_by
- validated_at
- validated_by

## FamenonaCompartimentLigne

- id
- famenona_id
- compartiment_id
- nombre_akoho
- sexe
- sante
- remarque

---

# 13. Priorité d'implémentation

## Phase 1 — Données de base

- [ ] Fournisseurs
- [ ] Races
- [ ] Poulaillers
- [ ] Compartiments
- [ ] Capacités

## Phase 2 — Bon de commande fournisseur

- [ ] Formulaire
- [ ] Calcul âge
- [ ] Calcul prix
- [ ] Validation
- [ ] Historique

## Phase 3 — Réception 1 Aéroport

- [ ] Liaison avec commande
- [ ] Saisie morts/perdus
- [ ] Calcul vivants
- [ ] Contrôle de conformité
- [ ] Validation

## Phase 4 — Réception 2 Poulailler

- [ ] Liaison avec Réception 1
- [ ] Saisie morts/vivants
- [ ] Contrôle des décès pendant le trajet
- [ ] Validation

## Phase 5 — Famenona compartiment

- [ ] Liste des compartiments
- [ ] Capacité restante
- [ ] Nombre
- [ ] Sexe
- [ ] Santé
- [ ] Plusieurs lignes par réception
- [ ] Validation globale

## Phase 6 — Traçabilité

- [ ] Historique complet
- [ ] Vue parcours du lot
- [ ] Filtres
- [ ] Audit des validations

---

# 14. Résultat final attendu

Un lot de poussins/poulets doit pouvoir être suivi de bout en bout :

`COMMANDE FOURNISSEUR`
→ `EXPÉDITION`
→ `RÉCEPTION 1 / AÉROPORT`
→ `TRANSPORT`
→ `RÉCEPTION 2 / POULAILLER`
→ `FAMENONA COMPARTIMENT`
→ `LOT CLÔTURÉ`

À tout moment, le système doit pouvoir expliquer :

- combien ont été commandés ;
- combien sont morts à l'aéroport ;
- combien sont perdus/manquants ;
- combien sont partis vivants de l'aéroport ;
- combien sont morts pendant le trajet ;
- combien sont arrivés vivants au poulailler ;
- dans quels compartiments ils ont été placés ;
- combien sont mâles/femelles ;
- leur niveau de santé ;
- la capacité restante des compartiments.