package tower;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import tower.obj.Ground;
import tower.obj.Road;
import tower.obj.Tower;
import tower.obj.Unit;
import tower.tiles.MapTile;
import tower.tiles.Point;
import tower.tiles.TileType;

public class Main {

	// 맵 정보를 담을 객체가 필요함.
	Map<Point, MapTile> map = new HashMap<>();

	// 실제 설치된 타워 목록
	List<Tower> towers = new ArrayList<>();

	// start point
	Point startPoint;

	// end point
	Point endPoint;

	// 살아있는 유닛들.
	Queue<Unit> liveUnits = new LinkedList<>();

	// 길정보.
	Set<Point> roadPoint = new HashSet<>();

	public static void main(String[] args) {
		Main game = new Main();
		try {
			// 맵불러오고
			game.loadMap();

			// 유닛배치를 임의로 한다.
			game.setTower();

			// 실제 게임 시작.
			game.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// game.testDrawMap();
		/*
		 * game.testUnitInfo(); game.testTowerInfo();
		 */
	}

	/**
	 * 지도를 불러온다.
	 * 
	 * @throws IOException
	 */
	private void loadMap() throws IOException {
		// 0=길, 1=타워건설가능, 2=시작지점, 3=종료지점.
		FileReader fr = new FileReader("map");
		BufferedReader br = new BufferedReader(fr);

		String line = null;
		int row = 0;
		while ((line = br.readLine()) != null) {
			// 줄단위로 돌면서
			// 칸 단위로 데이터를 입력한다.
			for (int i = 0; i < line.length(); i++) {
				// 해당 위치.
				Point p = new Point(row, i);

				// 타일 생성.
				MapTile tile = new MapTile();
				char c = line.charAt(i);
				int type = Integer.parseInt(String.valueOf(c));
				// 맵을 불러와서 타일에 바로 매핑시키면 되는데....
				switch (type) {
				case 0:
					tile.type = TileType.EmptyRoad;
					tile.obj = new Road();
					roadPoint.add(p);
					break;
				case 1:
					tile.type = TileType.EmptyGround;
					tile.obj = new Ground();
					break;
				case 2:
					tile.type = TileType.StartPoint;
					tile.obj = new Road();
					startPoint = p;
					roadPoint.add(p);
					break;
				case 3:
					tile.type = TileType.EndPoint;
					tile.obj = new Road();
					endPoint = p;
					roadPoint.add(p);
					break;

				default:
					break;
				}

				// 타일을 맵에 저장한다.
				map.put(p, tile);

			}
			row++;
		}
		br.close();

		// 맵 저장까지 완료.

	}

	/**
	 * 실제 게임 로직.
	 * 
	 * @throws Exception
	 */
	private void start() throws Exception {

		// main loop
		for (int turn = 1; turn <= 3; turn++) {
			System.out.println("##################");
			System.out.println("        "+turn);
			System.out.println("##################");
			//타워 건설 로직 넣고.
			
			while (true) {
				// 유닛 리스폰.
				respawn(turn);
				// 계산!!
				cal();
				// 화면 그리기/
				drawGame();
				// 턴이 종료되었으면 다음 턴으로.
				if (liveUnits.size() == 0) {
					break;
				}
			}
		}

	}

	/**
	 * 리스폰!!
	 */
	private void respawn(Integer turn) {
		TurnInfo t = TurnInfo.turns.get(turn);
		if (t.numberOfUnits >= 1) {
			// 유닛 추가는 시작 포인트에 해당 유닛을 추가해준다.
			Unit newUnit = new Unit(t.unitName);
			map.get(startPoint).obj = newUnit; // 요놈이 추가할 유닛.
			liveUnits.add(newUnit);
			t.numberOfUnits = t.numberOfUnits - 1;

		}
	}

	/**
	 * 타워를 짓는다.
	 */
	private void setTower() {
		// 1,2
		Point p1 = new Point(0, 1);
		Tower t1 = new Tower("tower1", p1);
		map.get(p1).obj = t1;
		towers.add(t1);
		// 3,3
		Point p2 = new Point(2, 2);
		Tower t2 = new Tower("tower1", p2);
		map.get(p2).obj = t2;
		towers.add(t2);
		// 1,5
		Point p3 = new Point(0, 4);
		Tower t3 = new Tower("tower1", p3);
		map.get(p3).obj = t3;
		towers.add(t3);
	}

	private void drawGame() throws InterruptedException {
		clearConsole();
		String[][] mapImage = new String[6][6];
		for (Entry<Point, MapTile> e : map.entrySet()) {
			Point p = e.getKey();
			mapImage[p.x][p.y] = e.getValue().obj.image;
		}

		// 맵출력.
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				System.out.print(mapImage[i][j]);
			}
			System.out.println();
		}
		Thread.sleep(1000);
	}

	// 실제 게임 계산 로직.
	private void cal() {
		// 유닛이 이동하고
		// 유닛이 이동하려면. 맵에 있는 모든 유닛을 찾아서. 이동시킨다.
		for (Unit u : liveUnits) {
			// 한놈씩 이동시킨다.
			u.moveForward(map, roadPoint);
		}

		// 타워가 적들을 공격
		for (Tower t : towers) {
			t.attackUnit(liveUnits);
		}

		// 죽은놈 처리.
		Set<Unit> deadUnits = new HashSet<>();
		for (Unit u : liveUnits) {
			System.out.println(u);
			if (u.hp <= 0) {
				// 살아있는 유닛 목록에서 제거.
				deadUnits.add(u);
				// 유닛이 있는 위치를 길로 바꿔준다.
				map.get(u.currentPosition).obj = new Road();
			}
		}
		// 죽여!
		liveUnits.removeAll(deadUnits);

	}

	private final static void clearConsole() {
		try {
			final String os = System.getProperty("os.name");

			if (os.contains("Windows")) {
				Runtime.getRuntime().exec("cls");
			} else {
				Runtime.getRuntime().exec("clear");
			}
		} catch (final Exception e) {
			e.printStackTrace();
			// Handle any exceptions.
		} finally {
			System.out.println();
			System.out.println();

		}
	}

}
