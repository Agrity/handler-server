import java.time.*;

public class WaterfallService {

	private final Offer offer;
	private final TemporalAmount delay;

	private LocalDateTime expirationTime;
	private Queue<Grower> growers;

	public WaterfallService(Offer offer, TemporalAmount delay) {
		this.offer = offer;
		this.delay = delay;
		expirationTime = LocalDateTime.now().plus(delay);
		this.growers = new Queue<>(offer.getAllGrowers());

	}


	public boolean process() {
		LocalDateTime current = LocalDateTime.now();	
		if(current.isAfter(expirationTime)) {

		}



	

		return true;
		


	}



}