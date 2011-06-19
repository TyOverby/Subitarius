def searchreplace(req,path,search,replace,exts=None):

    import fileinput, glob, string, sys, os
    from os.path import join
    # replace a string in multiple files
    #filesearch.py

    files = glob.glob(path + "/*")
    if files is not []:
        for file in files:
            if os.path.isfile(file):
                if exts is None or exts.count(os.path.splitext(file)[1]) is not 0:
                    for line in fileinput.input(file,inplace=1):
                        lineno = 0
                        lineno = string.find(line, search)
                        if lineno >0:
                            line =line.replace(search, replace)
                        sys.stdout.write(line)


