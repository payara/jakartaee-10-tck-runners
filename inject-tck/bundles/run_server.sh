echo "Downloading required TCK artifacts"
./download.sh
echo "Starting python3 server"
exec python3 -m http.server 8000