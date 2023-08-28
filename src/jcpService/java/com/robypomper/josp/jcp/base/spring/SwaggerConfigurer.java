/*******************************************************************************
 * The John Cloud Platform is the set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.jcp.base.spring;

import com.google.common.collect.Lists;
import com.robypomper.josp.info.JCPContacts;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Class to configure Swagger docs features on JCP APIs services.
 * <p>
 * This class get APIs group and sub-groups info from <code>Versions</code>
 * classes and allow transform them in Swagger Dockets and Swagger tags.
 * <p>
 * This class, also define, for Swagger environment:
 * AuthFlows: AuthCode, ClientCred, Implicit*
 * Scopes: obj, srv, mng
 */
@Configuration
@EnableSwagger2
public class SwaggerConfigurer {

    // Class Constants

    public static final String RES_PATH_DESC = "classpath:docs/api-%s-description.txt";

    public static final String ROLE_OBJ = "obj";
    public static final String ROLE_OBJ_SWAGGER = "role_obj";
    public static final String ROLE_OBJ_DESC = "Objects scope";
    public static final String ROLE_SRV = "srv";
    public static final String ROLE_SRV_SWAGGER = "role_srv";
    public static final String ROLE_SRV_DESC = "Services scope";
    public static final String ROLE_MNG = "mng";
    public static final String ROLE_MNG_SWAGGER = "role_mng";
    public static final String ROLE_MNG_DESC = "JCP Manager scope";
    public static final String ROLE_JCP = "jcp";
    public static final String ROLE_JCP_SWAGGER = "role_jcp";
    public static final String ROLE_JCP_DESC = "JCP Services scope";

    public static final String OAUTH_IMPL = "ImplicitCodeFlow";
    public static final String OAUTH_PASS = "AuthCodeFlow";
    public static final String OAUTH_CRED = "ClientCredentialsFlow";
    private static final String OAUTH_PATH_AUTH = com.robypomper.josp.defs.auth.keycloak.Paths20.FULL_PATH_AUTH;
    private static final String OAUTH_PATH_TOKEN = com.robypomper.josp.defs.auth.keycloak.Paths20.FULL_PATH_TOKEN;
    private static final String OAUTH_TOKEN_NAME = "token";
    private static final String OAUTH_CLIENT_ID = "";
    private static final String OAUTH_CLIENT_SECRET = "";

    public static final String OAUTH_FLOW_DEF_SRV = OAUTH_IMPL;
    public static final String OAUTH_FLOW_DEF_OBJ = OAUTH_IMPL;
    public static final String OAUTH_FLOW_DEF_MNG = OAUTH_IMPL;
    public static final String OAUTH_FLOW_DEF_JCP = OAUTH_IMPL;
    public static final String OAUTH_FLOW_DEF_TEST = OAUTH_IMPL;

    private static final Contact ContactJohn = new Contact(JCPContacts.getJohn().getFullName(), JCPContacts.getJohn().getUrl(), JCPContacts.getJohn().getEmail());


    // Internal vars

    @Value("${oauth2.url}")
    private String authUrl;


    // API's groups

    @Bean
    public Docket home() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(metadata(null))
                .select()
                .apis(RequestHandlerSelectors.none())
                .paths(PathSelectors.none())
                .build();
    }


    // API sets configurers

    public String getUrlBaseAuth() {
        return authUrl;
    }

    public Docket createAPIsGroup(String name, String path, String version, String titleSuffix) {
        APIGroup apiGroup = new APIGroup(name,path,version,titleSuffix, new APISubGroup[]{});
        return createAPIsGroup(apiGroup, authUrl);
    }

    public static Docket createAPIsGroup(APIGroup api, String urlBaseAuth) {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName(api.getName())
                .apiInfo(metadata(api))

                .forCodeGeneration(true)
                .useDefaultResponseMessages(false)
                .genericModelSubstitutes(ResponseEntity.class)

                .securitySchemes(Lists.newArrayList(
                        //oauthAuthCodeFlow(urlBaseAuth)
                        //,oauthClientCredentialFlow(urlBaseAuth)
                        oauthImplicitFlow(urlBaseAuth)
                ))

                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant(api.getPath()))
                .build()
                .useDefaultResponseMessages(false);

        Tag[] tags = subGroupsToTags(api.getSubGroups());
        if (tags.length > 0) {
            Tag tag = tags[0];
            tags = Arrays.copyOfRange(tags, 1, tags.length);
            docket.tags(tag, tags);
        }

        return docket;
    }

    private static OAuth oauthAuthCodeFlow(String urlBaseAuth) {
        List<GrantType> grants = new ArrayList<>();
        String urlAuth = urlBaseAuth + OAUTH_PATH_AUTH;
        String urlToken = urlBaseAuth + OAUTH_PATH_TOKEN;

        grants.add(new AuthorizationCodeGrant(
                new TokenRequestEndpoint(urlAuth,
                        OAUTH_CLIENT_ID,
                        OAUTH_CLIENT_SECRET),
                new TokenEndpoint(urlToken,
                        OAUTH_TOKEN_NAME)
        ));

        return new OAuth(OAUTH_PASS, getScopesList(), grants);
    }

    private static OAuth oauthClientCredentialFlow(String urlBaseAuth) {
        List<GrantType> grants = new ArrayList<>();

        grants.add(new ClientCredentialsGrant(
                urlBaseAuth + OAUTH_PATH_TOKEN
        ));

        return new OAuth(OAUTH_CRED, getScopesList(), grants);
    }

    private static OAuth oauthImplicitFlow(String urlBaseAuth) {
        List<GrantType> grants = new ArrayList<>();

        grants.add(new ImplicitGrant(
                new LoginEndpoint(urlBaseAuth + OAUTH_PATH_AUTH),
                OAUTH_TOKEN_NAME
        ));

        return new OAuth(OAUTH_IMPL, getScopesList(), grants);
    }

    private static List<AuthorizationScope> getScopesList() {
        return Arrays.asList(getScopes());
    }

    private static AuthorizationScope[] getScopes() {
        AuthorizationScope[] scopes = new AuthorizationScope[3];
        scopes[0] = new AuthorizationScope(ROLE_OBJ_SWAGGER, ROLE_OBJ_DESC);
        scopes[1] = new AuthorizationScope(ROLE_SRV_SWAGGER, ROLE_SRV_DESC);
        scopes[2] = new AuthorizationScope(ROLE_MNG_SWAGGER, ROLE_MNG_DESC);
        return scopes;
    }

    private static ApiInfo metadata(APIGroup api) {
        String title = api != null ? api.getTitle() : "JCP APIs";
        String description = api != null ? getDescription(api) : getLoadDescription(String.format(RES_PATH_DESC, "home"));
        String version = api != null ? api.getVersion() : "";

        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version(version)
                .contact(ContactJohn)
                .build();
    }

    private static Tag[] subGroupsToTags(APISubGroup[] subGroups) {
        Tag[] tags = new Tag[subGroups.length];
        for (int i = 0; i < subGroups.length; i++)
            tags[i] = new Tag(subGroups[i].getName(), subGroups[i].getDescription());
        return tags;
    }

    private static String getDescription(APIGroup api) {
        return getLoadDescription(api.getDescriptionPath());
    }

    private static String getLoadDescription(String resPath) {
        try {
            ResourceLoader loader = new DefaultResourceLoader();
            Resource res = loader.getResource(resPath);
            Reader reader = new InputStreamReader(res.getInputStream());
            return FileCopyUtils.copyToString(reader);

        } catch (IOException err) {
            return String.format("Description not found, check '%s' file.", resPath);
        }
    }


    // Data types

    @Data
    public static class APIGroup {

        private final String name;

        private final String path;

        private final String version;

        private final String titleSuffix;

        private final APISubGroup[] subGroups;


        public String getPath() {
            return String.format("%s/**", path);
        }

        public String getTitle() {
            return String.format("%s%s", titleSuffix.isEmpty() ? "" : titleSuffix + " - ", getName());
        }

        public String getDescriptionPath() {
            return String.format(RES_PATH_DESC, name).toLowerCase();
        }

    }

    @Data
    public static class APISubGroup {

        private final String name;
        private final String description;

    }

}
