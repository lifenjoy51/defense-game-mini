package tower.obj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import tower.tiles.Point;

public class Tower extends MapObject {
	public String name;
	public Integer damage;
	public Integer range;
	public Point position;

	public Tower() {
	}

	public Tower(String towerName, Point p) {
		Tower src = towerInfo.get(towerName);
		this.name = src.name;
		this.damage = src.damage;
		this.range = src.range;
		this.image = src.image;
		this.position = p;
	}

	// 타워정보. (타워이름, 타워)
	static Map<String, Tower> towerInfo = new HashMap<>();

	static {

		try {
			// 0=길, 1=타워건설가능, 2=시작지점, 3=종료지점.
			FileReader fr = new FileReader("tower-info");
			BufferedReader br = new BufferedReader(fr);

			String line = null;
			while ((line = br.readLine()) != null) {
				Tower t = new Tower();
				// 줄단위로 돌면서
				String[] info = line.split("\t");
				// 1은 이름
				t.name = info[0];
				// 2는 공격력
				t.damage = Integer.parseInt(info[1]);
				// 3은 범위
				t.range = Integer.parseInt(info[2]);
				// 4는 이미지.
				t.image = info[3];

				// 타워정보에 저장.
				towerInfo.put(t.name, t);

			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Tower [name=" + name + ", damage=" + damage + ", range="
				+ range + ", image=" + image + "]";
	}

	public void attackUnit(Queue<Unit> liveUnits) {
		//공격할 대상을 선택한다.
		//먼저 적부터 선택한다.
		for(Unit enemy : liveUnits){
			//공격할 대상은 나와의 거리가 사거리 보다 작거나 같아야 한다.
			int distance = (int) Math.sqrt(Math.pow(
					(this.position.x - enemy.currentPosition.x), 2)
					+ Math.pow((this.position.y - enemy.currentPosition.y), 2));
			System.out.println(this.position + "타워가 " + enemy.currentPosition+"에 있는 놈이랑 "+distance+"거리에 있다.");
			if(distance <= this.range){
				enemy.harmed(this.damage);
				//공격했으면 명령 종료!
				return;
			}
		}
	}

}
