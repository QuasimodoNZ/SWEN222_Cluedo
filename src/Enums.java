import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;




public class Enums {
	// The possible murder weapons
		public enum Weapon {
			ROPE, CANDLESTICK, KNIFE, PISTOL, BASEBALL_BAT, DUMBBELL, TROPHY, POISON, AXE;
			@Override
			public String toString() {
				switch (this) {
				case ROPE:
					return "Rope";
				case CANDLESTICK:
					return "Candlestick";
				case KNIFE:
					return "Knife";
				case PISTOL:
					return "Pistol";
				case BASEBALL_BAT:
					return "Baseball Bat";
				case DUMBBELL:
					return "Dumbbell";
				case TROPHY:
					return "Trophy";
				case POISON:
					return "Poison";
				case AXE:
					return "Axe";
				default:
					throw new IllegalArgumentException();
				}
			}

			/**
			 * Returns all the possible values of Board.Weapon as a list.
			 * 
			 * @return List<Weapon>
			 */
			public static List<Weapon> toList() {
				return new LinkedList<Weapon>(Arrays.asList(Weapon.values()));
			}

			public static Weapon toEnum(String name) {
				if (name.equalsIgnoreCase("rope"))
					return ROPE;
				if (name.equalsIgnoreCase("candlestick"))
					return CANDLESTICK;
				if (name.equalsIgnoreCase("knife"))
					return KNIFE;
				if (name.equalsIgnoreCase("pistol"))
					return PISTOL;
				if (name.equalsIgnoreCase("baseball bat"))
					return BASEBALL_BAT;
				if (name.equalsIgnoreCase("dumbbell"))
					return DUMBBELL;
				if (name.equalsIgnoreCase("trophy"))
					return TROPHY;
				if (name.equalsIgnoreCase("poison"))
					return POISON;
				if (name.equalsIgnoreCase("axe"))
					return AXE;

				throw new IllegalArgumentException();
			}
		}

		// The Rooms in the game
		public enum RoomName {
			SPA, THEATRE, LIVING_ROOM, OBSERVATORY, PATIO, SWIMMING_POOL, HALL, KITCHEN, DINING_ROOM, GUEST_HOUSE;
			@Override
			public String toString() {
				switch (this) {
				case SPA:
					return "Spa";
				case THEATRE:
					return "Theatre";
				case LIVING_ROOM:
					return "Living Room";
				case OBSERVATORY:
					return "Observatory";
				case PATIO:
					return "Patio";
				case SWIMMING_POOL:
					return "Swimming Pool";
				case HALL:
					return "Hall";
				case KITCHEN:
					return "Kitchen";
				case DINING_ROOM:
					return "Dining Room";
				case GUEST_HOUSE:
					return "Guest House";
				default:
					throw new IllegalArgumentException();
				}
			}

			/**
			 * Returns all the possible values of Board.RoomName as a list.
			 * 
			 * @return List<RoomName>
			 */
			public static List<RoomName> toList() {
				return new LinkedList<RoomName>(Arrays.asList(RoomName.values()));
			}

			public static RoomName toEnum(String name) {
				if (name.equalsIgnoreCase("spa"))
					return SPA;
				if (name.equalsIgnoreCase("theatre"))
					return THEATRE;
				if (name.equalsIgnoreCase("observatory"))
					return OBSERVATORY;
				if (name.equalsIgnoreCase("patio"))
					return PATIO;
				if (name.equalsIgnoreCase("swimming pool"))
					return SWIMMING_POOL;
				if (name.equalsIgnoreCase("hall"))
					return HALL;
				if (name.equalsIgnoreCase("kitchen"))
					return KITCHEN;
				if (name.equalsIgnoreCase("dining room"))
					return DINING_ROOM;
				if (name.equalsIgnoreCase("guest house"))
					return GUEST_HOUSE;

				throw new IllegalArgumentException();
			}
		}

		// The characters available to play
		public enum Character {
			JACK_MUSTARD, KASANDRA_SCARLETT, DIANE_WHITE, JACOB_GREEN, ELEANOR_PEACOCK, VICTOR_PLUM;
			@Override
			public String toString() {
				switch (this) {
				case JACK_MUSTARD:
					return "Jack Mustard";
				case KASANDRA_SCARLETT:
					return "Kasandra Scarlett";
				case DIANE_WHITE:
					return "Diane White";
				case JACOB_GREEN:
					return "Jacob Green";
				case ELEANOR_PEACOCK:
					return "Eleanor Peacock";
				case VICTOR_PLUM:
					return "Victor Plum";
				default:
					throw new IllegalArgumentException();
				}
			}

			/**
			 * Returns all the possible values of Board.Character as a list.
			 * 
			 * @return List<Character>
			 */
			public static List<Character> toList() {
				return new LinkedList<Character>(Arrays.asList(Character.values()));
			}

			public static Character toEnum(String name)
					throws IllegalArgumentException {
				if (name.equalsIgnoreCase("kasandra scarlett"))
					return Character.KASANDRA_SCARLETT;
				if (name.equalsIgnoreCase("jack mustard"))
					return Character.JACK_MUSTARD;
				if (name.equalsIgnoreCase("diane white"))
					return Character.DIANE_WHITE;
				if (name.equalsIgnoreCase("jacob green"))
					return Character.JACOB_GREEN;
				if (name.equalsIgnoreCase("eleanor peacock"))
					return Character.ELEANOR_PEACOCK;
				if (name.equalsIgnoreCase("victor plum"))
					return Character.VICTOR_PLUM;

				throw new IllegalArgumentException();
			}

			public String toBoardString() {
				switch (this) {
				case JACK_MUSTARD:
					return "Y";
				case KASANDRA_SCARLETT:
					return "R";
				case DIANE_WHITE:
					return "W";
				case JACOB_GREEN:
					return "G";
				case ELEANOR_PEACOCK:
					return "B";
				case VICTOR_PLUM:
					return "P";
				default:
					throw new IllegalArgumentException();
				}
			}
		}
}
