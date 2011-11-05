echo "Removing old version of subitarius"
rm -rf ~/subitarius 

echo "Moving current versoin to home directory"
cp ../subitarius* -r ~/

echo "Renaming the current subitarius to just \"subitarius\""
mv ~/subitarius* ~/subitarius/ 
