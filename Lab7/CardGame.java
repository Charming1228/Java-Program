import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class CardGame {
    static String[] suits = { "红桃", "黑桃", "方片", "梅花" }; // 红桃 > 黑桃 > 方片 > 梅花
    static Map<String, Integer> suitRank = Map.of("红桃", 4, "黑桃", 3, "方片", 2, "梅花", 1);
    static Map<String, Integer> valueRank = new HashMap<>();

    static {
        // 斗地主规则：3 < 4 < ... < K < A < 2
        String[] values = { "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A", "2" };
        for (int i = 0; i < values.length; i++) {
            valueRank.put(values[i], i + 1); // 数值越大，牌越大
        }
    }

    static class Card {
        String suit, value;

        Card(String suit, String value) {
            this.suit = suit;
            this.value = value;
        }

        int compare(Card other) {
            int valCompare = Integer.compare(valueRank.get(this.value), valueRank.get(other.value));
            if (valCompare != 0)
                return valCompare;
            return Integer.compare(suitRank.get(this.suit), suitRank.get(other.suit));
        }

        public String toString() {
            return suit + value;
        }
    }

    static class Player extends Thread {
        String name;
        List<Card> hand = new ArrayList<>();
        CyclicBarrier barrier;
        Card[] roundCards;
        Lock lock;
        Condition turn;
        int id;
        volatile static int currentPlayer = 0;

        public Player(int id, String name, CyclicBarrier barrier, Card[] roundCards, Lock lock, Condition turn) {
            this.id = id;
            this.name = name;
            this.barrier = barrier;
            this.roundCards = roundCards;
            this.lock = lock;
            this.turn = turn;
        }

        public void run() {
            try {
                for (int round = 0; round < 13; round++) {
                    lock.lock();
                    while (id != currentPlayer) {
                        turn.await();
                    }
                    Card card = hand.remove(0);
                    System.out.println(name + " 出牌: " + card);
                    roundCards[id] = card;
                    currentPlayer = (currentPlayer + 1) % 4;
                    turn.signalAll();
                    lock.unlock();
                    barrier.await(); // 等待所有人出完牌
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void startGame() throws Exception {
        List<Card> deck = new ArrayList<>();
        for (String suit : suits) {
            for (String value : valueRank.keySet()) {
                deck.add(new Card(suit, value));
            }
        }
        Collections.shuffle(deck);

        Card[] roundCards = new Card[4];
        CyclicBarrier barrier = new CyclicBarrier(4, () -> {
            int winner = 0;
            for (int i = 1; i < 4; i++) {
                if (roundCards[i].compare(roundCards[winner]) > 0) {
                    winner = i;
                }
            }
            System.out.println("本轮胜者是：玩家 " + (winner + 1) + " 出了 " + roundCards[winner]);
            Player.currentPlayer = winner;
        });

        Lock lock = new ReentrantLock();
        Condition turn = lock.newCondition();

        Player[] players = new Player[4];
        for (int i = 0; i < 4; i++) {
            players[i] = new Player(i, "玩家" + (i + 1), barrier, roundCards, lock, turn);
        }

        for (int i = 0; i < 52; i++) {
            players[i % 4].hand.add(deck.get(i));
        }

        for (Player player : players) {
            player.start();
        }
    }

    public static void main(String[] args) throws Exception {
        startGame();
    }
}
