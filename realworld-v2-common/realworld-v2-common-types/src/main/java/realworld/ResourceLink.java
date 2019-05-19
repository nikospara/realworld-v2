package realworld;

/**
 * An immutable link to another resource.
 */
public class ResourceLink {

	private String name;
	private String href;

	public ResourceLink(String name, String href) {
		this.name = name;
		this.href = href;
	}

	/**
	 * The display name of the resource.
	 *
	 * @return The display name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The link to the resource.
	 *
	 * @return The link to the resource, a URL
	 */
	public String getHref() {
		return href;
	}
}
