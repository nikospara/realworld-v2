package realworld.user.jaxrs.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonString;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.resourcefactory.SingletonResource;
import org.jboss.resteasy.spi.metadata.DefaultResourceClass;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.jaxrs.sys.ObjectMapperProvider;
import realworld.jaxrs.sys.exceptionmap.EntityDoesNotExistExceptionMapper;
import realworld.test.jaxrs.CustomMockDispatcherFactory;
import realworld.user.jaxrs.FollowsResource;
import realworld.user.services.FollowService;

/**
 * Tests for the {@link FollowsResourceImpl}.
 */
@EnableAutoWeld
@AddBeanClasses(ObjectMapperProvider.class)
@AddExtensions(ResteasyCdiExtension.class)
@ActivateScopes(RequestScoped.class)
@ExtendWith(MockitoExtension.class)
public class FollowsResourceImplTest {

	private static final String APPLICATION_PATH = "/api/current";
	private static final String USERNAME_OF_FOLLOWER = "USERNAME_OF_FOLLOWER";
	private static final String USERNAME_TO_FOLLOW = "USERNAME_TO_FOLLOW";

	@Produces @Mock
	private FollowService followService;

	@Inject
	private FollowsResourceImpl sut;

	private Dispatcher dispatcher;

	private MockHttpResponse response;

	@BeforeEach
	void init() {
		dispatcher = CustomMockDispatcherFactory.createDispatcher(ObjectMapperProvider.class, EntityDoesNotExistExceptionMapper.class);
		SingletonResource resourceFactory = new SingletonResource(sut, new DefaultResourceClass(FollowsResource.class, null));
		dispatcher.getRegistry().addResourceFactory(resourceFactory, APPLICATION_PATH);
		response = new MockHttpResponse();
	}

	@Test
	void testFindAllFollowed() throws Exception {
		MockHttpRequest request = MockHttpRequest.get(APPLICATION_PATH + "/users/" + USERNAME_OF_FOLLOWER + "/follows/all");

		when(followService.findAllFollowed(USERNAME_OF_FOLLOWER)).thenReturn(Arrays.asList("name1","name2"));

		dispatcher.invoke(request, response);

		assertEquals(200, response.getStatus());
		ArgumentCaptor<String> followerCaptor = ArgumentCaptor.forClass(String.class);
		verify(followService).findAllFollowed(followerCaptor.capture());
		assertEquals(USERNAME_OF_FOLLOWER, followerCaptor.getValue());
		JsonReader jsonReader = Json.createReader(new StringReader(response.getContentAsString()));
		List<String> result = jsonReader.readArray().getValuesAs(JsonString.class).stream().map(JsonString::getString).collect(Collectors.toList());
		assertEquals(Arrays.asList("name1","name2"), result);
	}

	@Test
	void testCheckFollowedWithParam() throws Exception {
		MockHttpRequest request = MockHttpRequest.get(APPLICATION_PATH + "/users/" + USERNAME_OF_FOLLOWER + "/follows?u=a&u=b&u=c");

		Map<String,Boolean> map = new HashMap<>();
		map.put("a", true);
		map.put("b", false);
		map.put("c", true);
		when(followService.checkAllFollowed(USERNAME_OF_FOLLOWER, Arrays.asList("a","b","c"))).thenReturn(map);

		dispatcher.invoke(request, response);

		assertEquals(200, response.getStatus());
		JsonReader jsonReader = Json.createReader(new StringReader(response.getContentAsString()));
		Map<String, Boolean> result = jsonReader.readObject().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Boolean.valueOf(e.getValue().toString())));
		assertEquals(map, result);
	}

	@Test
	void testFollows() throws Exception {
		MockHttpRequest request = MockHttpRequest.get(APPLICATION_PATH + "/users/" + USERNAME_OF_FOLLOWER + "/follows/" + USERNAME_TO_FOLLOW);

		dispatcher.invoke(request, response);

		assertEquals(200, response.getStatus());
		ArgumentCaptor<String> followerCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> followedCaptor = ArgumentCaptor.forClass(String.class);
		verify(followService).follows(followerCaptor.capture(), followedCaptor.capture());
		assertEquals(USERNAME_OF_FOLLOWER, followerCaptor.getValue());
		assertEquals(USERNAME_TO_FOLLOW, followedCaptor.getValue());
		assertEquals("false", response.getContentAsString());
	}

	@Test
	void testFollow() throws Exception {
		MockHttpRequest request = MockHttpRequest.post(APPLICATION_PATH + "/users/" + USERNAME_OF_FOLLOWER + "/follows/" + USERNAME_TO_FOLLOW);

		dispatcher.invoke(request, response);

		assertEquals(204, response.getStatus());
		ArgumentCaptor<String> followerCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> followedCaptor = ArgumentCaptor.forClass(String.class);
		verify(followService).follow(followerCaptor.capture(), followedCaptor.capture());
		assertEquals(USERNAME_OF_FOLLOWER, followerCaptor.getValue());
		assertEquals(USERNAME_TO_FOLLOW, followedCaptor.getValue());
	}

	@Test
	void testUnfollow() throws Exception {
		MockHttpRequest request = MockHttpRequest.delete(APPLICATION_PATH + "/users/" + USERNAME_OF_FOLLOWER + "/follows/" + USERNAME_TO_FOLLOW);

		dispatcher.invoke(request, response);

		assertEquals(204, response.getStatus());
		ArgumentCaptor<String> followerCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> followedCaptor = ArgumentCaptor.forClass(String.class);
		verify(followService).unfollow(followerCaptor.capture(), followedCaptor.capture());
		assertEquals(USERNAME_OF_FOLLOWER, followerCaptor.getValue());
		assertEquals(USERNAME_TO_FOLLOW, followedCaptor.getValue());
	}
}
