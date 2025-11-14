# server init

```
touch application-hm-server.yml
nano ...
```

### GitHub token "ghcr-registry-token"

```
read -p "Provide GitHub Access Token: " GITHUB_TOKEN && \
  curl -H "Authorization: token $GITHUB_TOKEN" -H "Accept: application/vnd.github.v3.raw" -O -L \
  https://raw.githubusercontent.com/tech1-agency/devkit/main/installations/jbst-hm-server/install.sh
chmod +x install.sh
./install.sh
rm install.sh
```



