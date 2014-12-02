package tower.obj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import tower.tiles.MapTile;
import tower.tiles.Point;

public class Unit extends MapObject {
	public String name;
	public Integer hp;
	public Integer speed;

	static private Map<String, Unit> units = new HashMap<>();

	static {

		try {
			// 0=길, 1=타워건설가능, 2=시작지점, 3=종료지점.
			FileReader fr = new FileReader("unit-info");
			BufferedReader br = new BufferedReader(fr);

			String line = null;
			while ((line = br.readLine()) != null) {
				Unit u = new Unit();
				// 줄단위로 돌면서
				String[] info = line.split("\t");
				// 1은 이름
				u.name = info[0];
				// 2는 hp.
				u.hp = Integer.parseInt(info[1]);
				// 3은 속도
				u.speed = Integer.parseInt(info[2]);
				// 4는 이미지.
				u.image = info[3];

				// 저장.
				units.put(u.name, u);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Unit() {
	}

	public Unit(String name) {
		Unit src = units.get(name);
		this.name = src.name;
		this.hp = src.hp;
		this.speed = src.speed;
		this.image = src.image;
	}

	@Override
	public String toString() {
		return "Unit [name=" + name + ", hp=" + hp + ", speed=" + speed
				+ ", image=" + image + "]";
	}
	
	//이동계산을 위한 변수들.
	Point lastPosition;
	public Point currentPosition;

	/**
	 * 맵에서 유닛을 이동시킨다.
	 * 
	 * @param map
	 * @param roadPoint 
	 */
	public void moveForward(Map<Point, MapTile> map, Set<Point> roadPoint) {
		//길인 위치만 저장해놓는다.
		if(currentPosition==null){
			for(Entry<Point, MapTile> e : map.entrySet()){
				Point p = e.getKey();
				//자기위치를 찾는다.
				if(e.getValue().obj.equals(this)){
					currentPosition = p;
				}
			}
			//한바퀴 돌고서도 자기 위치를 못받아온다면??
			if(currentPosition==null){
				System.err.println("이상!!!");
			}
		}
		//이전 이동위치를 확인한다.
		if(lastPosition==null){
			lastPosition = currentPosition;
		}
		//파라미터로 받아온 맵에서 현재 유닛을 이동시킨다.
		//현재 위치로 부터 근방의 9방향 위치를 받아온다.
		//현재 위치를 알고 있고. 비교한다.
		Set<Point> nearPoint = new HashSet<>();
		for(Entry<Point, MapTile> e : map.entrySet()){
			Point comparePoint = e.getKey();
			// 현재 위치의 x좌표나 y좌표가, 비교대상의 x좌표나 y좌표와 1의 차이가 있을 때.
			boolean nearX = (Math.abs(comparePoint.x - currentPosition.x) <= 1)
					&& (Math.abs(comparePoint.y - currentPosition.y) == 0);
			boolean nearY = (Math.abs(comparePoint.x - currentPosition.x) == 0)
					&& (Math.abs(comparePoint.y - currentPosition.y) <= 1);
			if (nearX || nearY) {
				nearPoint.add(comparePoint);
			}
		}
		
		//앞으로 가야할 위치를 찾는다.
		Point forwardPoint = null;
		for(Point p : nearPoint){
			//점 중에서. 길이고, 내가 지나오지 않은 길이여야 한다.
			if (roadPoint.contains(p)
					&& (!p.equals(lastPosition) && !p.equals(currentPosition))) {
				forwardPoint = p;
			}
		}
		
		//지금 유닛을 앞으로 가야할 위치에 옮긴다.
		map.get(forwardPoint).obj = this;
		//이전칸을 비워놔야지.
		map.get(currentPosition).obj = new Road();
		//이전 위치를 현재 위치로 지정한다.
		lastPosition = currentPosition;
		//현재위치를 이동할 위치로 바꾼다.
		currentPosition = forwardPoint;
		
	}

	/**
	 * 공격받다.
	 * @param damage
	 */
	public void harmed(Integer damage) {
		this.hp = this.hp - damage;
	}

}
