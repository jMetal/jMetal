{ 
  if($0 ~ /#/)
    print $0
  else {
    for(i=1; i <= NF; i++) {
      if($i == 0) 
        printf "%f ", 10000 
        #printf ""
      else 
        printf "%f ", 1.0/$i
    }
    print ""
  }
}
